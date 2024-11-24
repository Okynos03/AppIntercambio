package com.example.appintercambio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RaffleInfoActivity extends AppCompatActivity {

    private LinearLayout resultListLayout;
    private DatabaseReference dbReference;
    private Button buttonContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raffle_info);

        buttonContinuar = findViewById(R.id.buttonContinuar);
        resultListLayout = findViewById(R.id.resultListLayout);

        // Inicializa la referencia a Firebase
        dbReference = FirebaseDatabase.getInstance().getReference("sorteo");

        // Carga los datos del sorteo
        loadRaffleResults();

        buttonContinuar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Poner aquí la conexion del botón continuar
            }
        });
    }

    private void loadRaffleResults() {
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultListLayout.removeAllViews(); // Limpia la lista antes de llenarla

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    Toast.makeText(RaffleInfoActivity.this, "No hay resultados del sorteo disponibles", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot item : snapshot.getChildren()) {
                    String from = item.child("from").getValue(String.class);
                    String to = item.child("to").getValue(String.class);

                    if (from != null && to != null) {
                        // LinearLayout para alinear from, flecha y to
                        LinearLayout rowLayout = new LinearLayout(RaffleInfoActivity.this);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // Emisor
                        TextView fromTextView = new TextView(RaffleInfoActivity.this);
                        LinearLayout.LayoutParams fromParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );
                        fromParams.setMargins(8, 0, 8, 0); // Margen entre el emisor y la flecha
                        fromTextView.setLayoutParams(fromParams);
                        fromTextView.setText(from);
                        fromTextView.setTextSize(16);
                        fromTextView.setGravity(Gravity.CENTER);

                        // Flecha
                        TextView arrowTextView = new TextView(RaffleInfoActivity.this);
                        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        arrowParams.setMargins(16, 0, 16, 0); // Margen alrededor de la flecha
                        arrowTextView.setLayoutParams(arrowParams);
                        arrowTextView.setText("----->");
                        arrowTextView.setTextSize(16);
                        arrowTextView.setGravity(Gravity.CENTER);

                        // Receptor
                        TextView toTextView = new TextView(RaffleInfoActivity.this);
                        LinearLayout.LayoutParams toParams = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );
                        toParams.setMargins(8, 0, 8, 0); // Margen entre la flecha y el receptor
                        toTextView.setLayoutParams(toParams);
                        toTextView.setText(to);
                        toTextView.setTextSize(16);
                        toTextView.setGravity(Gravity.CENTER);

                        // Añade los TextViews al LinearLayout
                        rowLayout.addView(fromTextView);
                        rowLayout.addView(arrowTextView);
                        rowLayout.addView(toTextView);

                        // Añade la fila al layout principal
                        resultListLayout.addView(rowLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error al obtener datos: " + error.getMessage());
                Toast.makeText(RaffleInfoActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
