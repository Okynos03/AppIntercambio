package com.example.appintercambio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appintercambio.Adapters.ExchangeAdapter;
import com.example.appintercambio.Adapters.ParticipantAdapter;
import com.example.appintercambio.Models.Exchange;
import com.example.appintercambio.Models.Participant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroParticipantesActivity  extends AppCompatActivity {

    private static int lastId = 0;

    private EditText editTextNombre;
    private EditText editTextCorreo;
    private Button buttonRegistrarParticipante;
    private Button buttonContinuarP;

    //Firebase stuff
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private List<Participant> participants = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_participantes);
        initializeViews();
        setupButtons();

        select();//to know if this activity should be shown
    }

    private void initializeViews() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        buttonRegistrarParticipante = findViewById(R.id.buttonRegistrarParticipante);
        buttonContinuarP = findViewById(R.id.buttonContinuarP);
    }

    private void setupButtons() {
        //buttonRegistrarParticipante.setEnabled(false);

        buttonContinuarP.setEnabled(false);
        buttonContinuarP.setBackgroundColor(getResources().getColor(R.color.nobutton));
        buttonContinuarP.setOnClickListener(v -> {
            vibrate();
            Intent intent = new Intent(this, RegistroIntercambioActivity.class);
            startActivity(intent);
        });

        buttonRegistrarParticipante.setOnClickListener(v -> {
            vibrate();
            printFieldsInfo();
            insert();
        });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 0, 20, 0, 0, 20};
        vibrator.vibrate(pattern, -1);
    }

    //Log para seguiminto de la informacion
    private void printFieldsInfo() {
        Log.d("INFO_CAMPOS", "Nombre: " + editTextNombre.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Correo: " + editTextCorreo.getText().toString().trim());
    }

    private void insert(){
        String name = editTextNombre.getText().toString().trim();
        String email = editTextCorreo.getText().toString().trim();

        if (validateFields(name, email)) {

            Participant participant = new Participant(createId(), email, name);
            DatabaseReference ref = db.getReference("participante/" + participant.getId());

            ref.setValue(participant).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Participante registrado con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al registrar participante", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private int createId(){
        lastId++;
        return lastId;
    }

    private boolean validateFields(String name, String email) {
        if (name.isEmpty() && email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre y un correo electrónico", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.matches(".+@.+\\..+")) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true; // Si todas las validaciones pasan
    }

    private void validateShow(){
        if(participants.size() > 1){
            Intent intent = new Intent(this, RegistroIntercambioActivity.class);
            startActivity(intent);
        }
    }

    private void select() {
        DatabaseReference ref = db.getReference("participante/");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    participants.clear();

                    for (DataSnapshot item : snapshot.getChildren()) {
                        try {
                            if (item.exists() && item.hasChildren()) {
                                Participant participant = item.getValue(Participant.class);
                                participants.add(participant);
                            } else {
                                Log.w("FirebaseWarning", "Empty or invalid exchange data: " + item.getKey());
                            }
                        } catch (DatabaseException e) {
                            Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                            Log.e("FirebaseError", "DataSnapshot value: " + item.getValue());
                        }
                    }
                    //ParticipantAdapter adapter = new ParticipantAdapter(RegistroParticipantesActivity.this, participants);
                    //listView.setAdapter(adapter);

                } else {
                    Log.e("FirebaseError", "Error getting data: " + task.getException());
                }
                validateShow();
            }
        });
    }

}