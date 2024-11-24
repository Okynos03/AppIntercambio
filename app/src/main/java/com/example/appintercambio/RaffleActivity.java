package com.example.appintercambio;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appintercambio.Models.Participant;
import com.example.appintercambio.algorithms.raffle_algorithm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaffleActivity extends AppCompatActivity {
    private TextView tvProgreso;
    private Button btnSortear;
    private Button btnVerResultados;
    private boolean isRaffleCompleted = false;
    private raffle_algorithm sorteo;
    private List<Participant> participantList = new ArrayList<>();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private int n_sorteo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raffle);

        tvProgreso = findViewById(R.id.tvProgreso);
        btnSortear = findViewById(R.id.btnSortear);
        btnVerResultados = findViewById(R.id.btnVerResultados);
        sorteo = new raffle_algorithm();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Deshabilita el botón "Ver Resultados" inicialmente
        setButtonState(btnVerResultados, false);

        btnSortear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonState(btnSortear, false);
                selectParticipants(); // Primero obtenemos los participantes
            }
        });

        // Te lleva a la activity de resultados
        btnVerResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRaffleCompleted) {
                    Intent intent = new Intent(RaffleActivity.this, RaffleInfoActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RaffleActivity.this, "Debes realizar el sorteo antes de ver los resultados",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        select();//To verify if a raffle was done
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void selectParticipants() {
        DatabaseReference ref = db.getReference("participante/");
        tvProgreso.setText("Obteniendo participantes...");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    participantList.clear();

                    Log.d("Sorteo", "Número de participantes encontrados: " + snapshot.getChildrenCount());

                    for (DataSnapshot item : snapshot.getChildren()) {
                        try {
                            if (item.exists() && item.hasChildren()) {
                                Participant participant = item.getValue(Participant.class);
                                if(participant != null) {
                                    participantList.add(participant);
                                    Log.d("Sorteo", "Participante agregado: " + participant.getName());
                                }
                            }
                        } catch (DatabaseException e) {
                            Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                        }
                    }

                    if(participantList.isEmpty()) {
                        Log.e("Sorteo", "No se encontraron participantes");
                        tvProgreso.setText("No hay participantes para sortear");
                        btnSortear.setAlpha(1.0f);
                        btnSortear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5FDC3")));
                        setButtonState(btnSortear, true);
                        return;
                    }

                    iniciarAlgoritmoDeSorteo();

                } else {
                    Log.e("FirebaseError", "Error getting data: " + task.getException());
                    tvProgreso.setText("Error al obtener participantes");
                    btnSortear.setAlpha(1.0f);
                    btnSortear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5FDC3")));
                    setButtonState(btnSortear, true);
                }
            }
        });
    }

    private void iniciarAlgoritmoDeSorteo() {
        // Aquí se ejecuta el algoritmo de sorteo
        // Simulacion del algoritmo para fines practicos xd
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simula el progreso mientras se realiza el sorteo real
                for (int i = 1; i <= 90; i++) {
                    final int progreso = i;
                    // Actualiza el TextView en el hilo de la interfaz de usuario
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvProgreso.setText("Progreso: " + progreso + "%");
                        }
                    });

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Realiza el sorteo real
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        realizarSorteoReal();
                    }
                });
            }
        }).start();
    }

    private void realizarSorteoReal() {
        sorteo.realizarSorteo(participantList, new raffle_algorithm.SorteoCallback() {
            @Override
            public void onSorteoCompleto(Map<String, String> resultado) {
                tvProgreso.setText("Progreso: 100%");
                Toast.makeText(RaffleActivity.this,
                        "Sorteo completado exitosamente",
                        Toast.LENGTH_SHORT).show();

                btnVerResultados.setAlpha(1.0f);
                btnVerResultados.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5FDC3"))); // Verde// Verde
                setButtonState(btnVerResultados, true);
                isRaffleCompleted = true;
            }

            @Override
            public void onError(Exception e) {
                tvProgreso.setText("Error en el sorteo");
                Toast.makeText(RaffleActivity.this,
                        "Error al realizar el sorteo: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("Sorteo", "Error en el sorteo", e);
                btnSortear.setAlpha(1.0f);
                btnSortear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5FDC3")));
                setButtonState(btnSortear, true);

            }
        });
    }

    private void setButtonState(Button button, boolean enabled) {
        button.setEnabled(enabled);
        if (!enabled) {  // Solo cuando está deshabilitado
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.nobutton)));
            button.setAlpha(0.5f);
        }
    }

    private void validateShow(){
        if(n_sorteo > 0){
            Intent intent = new Intent(this, RaffleInfoActivity.class);
            startActivity(intent);
        }
    }

    private void select() {
        DatabaseReference ref = db.getReference("sorteo/");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    for (DataSnapshot item : snapshot.getChildren()) {
                        try {
                            if (item.exists() && item.hasChildren()) {
                                n_sorteo++;
                            } else {
                                Log.w("FirebaseWarning", "Empty or invalid exchange data: " + item.getKey());
                            }
                        } catch (DatabaseException e) {
                            Log.e("FirebaseError", "Error converting data: " + e.getMessage());
                            Log.e("FirebaseError", "DataSnapshot value: " + item.getValue());
                        }
                    }

                } else {
                    Log.e("FirebaseError", "Error getting data: " + task.getException());
                }
                validateShow();
            }
        });
    }
}
