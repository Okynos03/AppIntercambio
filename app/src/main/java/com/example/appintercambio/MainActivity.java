package com.example.appintercambio;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appintercambio.Adapters.ExchangeAdapter;
import com.example.appintercambio.Models.Exchange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private List<Exchange> exchangeList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        //insert();
        //update(1);
        //select();

    }

    private void select() {
        DatabaseReference ref = db.getReference("evento/");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    exchangeList.clear();

                    for (DataSnapshot item : snapshot.getChildren()) {
                        try {
                            if (item.exists() && item.hasChildren()) {
                                Exchange exchange = item.getValue(Exchange.class);
                                exchangeList.add(exchange);
                            } else {
                                Log.w("FirebaseWarning", "Empty or invalid exchange data: " + item.getKey());
                            }
                        } catch (DatabaseException e) {
                            Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                            Log.e("FirebaseError", "DataSnapshot value: " + item.getValue());
                        }
                    }
                    ExchangeAdapter adapter = new ExchangeAdapter(MainActivity.this, exchangeList);
                    listView.setAdapter(adapter);

                } else {
                    Log.e("FirebaseError", "Error getting data: " + task.getException());
                }
            }
        });
    }

    private void insert(){
        Exchange exchange = new Exchange(3, "Dembele", "Gol", "2024-01-01", "13:00", 10, 20);

        DatabaseReference ref = db.getReference("evento/" + exchange.getId());
        ref.setValue(exchange).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void remove(int id){
        DatabaseReference ref = db.getReference("evento/" + id);
        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void update(int id){
        DatabaseReference ref = db.getReference("evento/" + id);
        Map<String, Object> updates = new HashMap<>();
        updates.put("date", "2024-12-25");
        updates.put("time", "10:00");

        ref.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}