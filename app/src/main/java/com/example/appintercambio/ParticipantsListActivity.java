package com.example.appintercambio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;

import com.example.appintercambio.Adapters.ParticipantAdapter;
import com.example.appintercambio.Models.Participant;
import com.example.appintercambio.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParticipantsListActivity extends AppCompatActivity{

    private ListView listViewParticipants;
    private Button buttonContinuar;
    private List<Participant> participantList;
    private ParticipantAdapter adapter;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeViews();
        setupButtons();
        initializeData();
        loadParticipants();
    }

    private void initializeViews() {
        listViewParticipants = findViewById(R.id.listViewParticipants);
        buttonContinuar = findViewById(R.id.buttonContinuar);
    }

    private void setupButtons() {
        buttonContinuar.setOnClickListener(v -> {
            // Aquí la lógica para continuar
            // Por ejemplo, iniciar la siguiente actividad
            Intent intent = new Intent(this, RaffleActivity.class);
            startActivity(intent);
        });
    }

    private void initializeData() {
        db = FirebaseDatabase.getInstance();
        participantList = new ArrayList<>();
        adapter = new ParticipantAdapter(this, participantList);
        listViewParticipants.setAdapter(adapter);
    }

    private void loadParticipants() {
        DatabaseReference ref = db.getReference("participante/");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    List<Participant> newParticipants = new ArrayList<>();

                    for (DataSnapshot item : snapshot.getChildren()) {
                        try {
                            if (item.exists() && item.hasChildren()) {
                                Participant participant = item.getValue(Participant.class);
                                if (participant != null) {
                                    newParticipants.add(participant);
                                }
                            } else {
                                Log.w("FirebaseWarning", "Empty or invalid participant data: " + item.getKey());
                            }
                        } catch (DatabaseException e) {
                            Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                            Log.e("FirebaseError", "DataSnapshot value: " + item.getValue());
                        }
                    }

                    // Ordenar los participantes orden alfabético
                    Collections.sort(newParticipants, new Comparator<Participant>() {
                        @Override
                        public int compare(Participant p1, Participant p2) {
                            return p1.getName().compareToIgnoreCase(p2.getName());
                        }
                    });

                    participantList.clear();
                    participantList.addAll(newParticipants);
                    adapter.notifyDataSetChanged();


                } else {
                    Log.e("FirebaseError", "Error getting data: " + task.getException());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
