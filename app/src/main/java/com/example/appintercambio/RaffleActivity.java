package com.example.appintercambio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RaffleActivity extends AppCompatActivity {
    private TextView tvProgreso;
    private Button btnSortear;
    private Button btnVerResultados;
    private boolean isRaffleCompleted = false; // comprobar que se hizo el sorteo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raffle);

        tvProgreso = findViewById(R.id.tvProgreso);
        btnSortear = findViewById(R.id.btnSortear);
        btnVerResultados = findViewById(R.id.btnVerResultados);

        // Deshabilita el botón "Ver Resultados" inicialmente
        //btnVerResultados.setEnabled(false);

        // Botón para iniciar el algoritmo de sorteo
        btnSortear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSortear.setEnabled(false);
                iniciarAlgoritmoDeSorteo();
            }
        });

        // Te lleva a la activity de resultados
        btnVerResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRaffleCompleted) {
                    System.out.println("holaaaaaaaa");
                    // Inicia la nueva actividad
                    //Intent intent = new Intent(RaffleActivity.this, ResultadoActivity.class);
                    //startActivity(intent);
                } else {
                    // Muestra un Toast indicando que primero debes realizar el sorteo
                    Toast.makeText(RaffleActivity.this, "Debes realizar el sorteo antes de ver los resultados", Toast.LENGTH_SHORT).show();
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
                // Simula pasos del algoritmo
                for (int i = 1; i <= 100; i++) {
                    final int progreso = i;

                    // Actualiza el TextView en el hilo de la interfaz de usuario
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvProgreso.setText("Progreso: " + progreso + "%");

                            // Habilita el boton Ver Resultados cuando el progreso llega al 100%
                            if (progreso == 100) {
                                btnVerResultados.setEnabled(true);
                                isRaffleCompleted = true; // Establece que el sorteo ha sido completado
                            }
                        }
                    });

                    // Simula tiempo de procesamiento
                    try {
                        Thread.sleep(25); // Pausa de 50 milisegundos
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
