package com.example.appintercambio;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private Button buttonGenerarEmail;
    private EmailGenerator emailGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raffle_info);

        buttonContinuar = findViewById(R.id.buttonContinuar);
        resultListLayout = findViewById(R.id.resultListLayout);
        buttonGenerarEmail = findViewById(R.id.buttonGenerarEmail);
        emailGenerator = new EmailGenerator();

        // Inicializa la referencia a Firebase
        dbReference = FirebaseDatabase.getInstance().getReference("sorteo");

        setupButtons();
        // Carga los datos del sorteo
        loadRaffleResults();

    }

    private void setupButtons() {
        buttonGenerarEmail.setOnClickListener(v -> generateEmails());
        buttonContinuar.setOnClickListener(v -> {
            // Lógica para el botón continuar
        });
    }

    private void setButtonState(Button button, boolean enabled) {
        button.setEnabled(enabled);
        if (!enabled) {  // Solo cuando está deshabilitado
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.nobutton)));
            button.setAlpha(0.5f);
        }
    }


    private void generateEmails() {
        // Deshabilitar el botón inmediatamente después del clic
        setButtonState(buttonGenerarEmail, false);

        Toast.makeText(this, "Generando correos...", Toast.LENGTH_SHORT).show();

        emailGenerator.generateEmails(emailDataList -> {
            if (emailDataList != null && !emailDataList.isEmpty()) {
                Toast.makeText(RaffleInfoActivity.this,
                        "Se generaron " + emailDataList.size() + " correos exitosamente",
                        Toast.LENGTH_LONG).show();
                //Milo, si usaste lo de java mail, creo que se pude usar de l asiguiente forma, DEPENDE DE COMO HICISTE TUS CLASES

                //final int[] emailsSent = {0};
                //final int totalEmails = emailDataList.size();

                for (EmailGenerator.EmailData emailData : emailDataList) {
                    Log.d("EmailGeneration", "Email para: " + emailData.getToEmail());
                    Log.d("EmailGeneration", "Contenido: " + emailData.getEmailContent());


                    // EJEMPLO DE USO SUPONIENDO QUE TIENE UNA CLASE QUE SE LLAME EMAILSENDER
                    //EmailSender.sendEmail(
                    //        emailData.getToEmail(),
                    //        "Información del Intercambio Navideño",
                    //        emailData.getEmailContent(),
                    //        new EmailSender.EmailCallback() {
                    //            @Override
                    //            public void onSuccess() {
                    //                emailsSent[0]++;
                    //                if (emailsSent[0] == totalEmails) {
                    //                    Toast.makeText(RaffleInfoActivity.this,
                    //                            "Todos los correos fueron enviados exitosamente",
                    //                            Toast.LENGTH_LONG).show();
                    //                }
                    //            }
                    //        }
                }

                // El botón permanece deshabilitado después de generar los correos exitosamente

            } else {
                Toast.makeText(RaffleInfoActivity.this,
                        "Error al generar los correos",
                        Toast.LENGTH_LONG).show();

                buttonGenerarEmail.setAlpha(1.0f);
                buttonGenerarEmail.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5FDC3")));
                setButtonState(buttonGenerarEmail, true);
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
