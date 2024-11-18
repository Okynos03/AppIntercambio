package com.example.appintercambio;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroParticipantesActivity  extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextCorreo;
    private Button buttonRegistrarParticipante;
    private Button buttonContinuarP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_participantes);
        initializeViews();
        setupVibrations();
    }

    private void initializeViews() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        buttonRegistrarParticipante = findViewById(R.id.buttonRegistrarParticipante);
        buttonContinuarP = findViewById(R.id.buttonContinuarP);
    }

    //Log para seguiminto de la informacion
    private void printFieldsInfo() {
        Log.d("INFO_CAMPOS", "Nombre: " + editTextNombre.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Correo: " + editTextCorreo.getText().toString().trim());
    }

    private void setupVibrations() {
        buttonRegistrarParticipante.setOnClickListener(v -> {
            vibrate();
            printFieldsInfo();
        });
        buttonContinuarP.setOnClickListener(v -> {
            vibrate();
            printFieldsInfo();
        });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 0, 20, 0, 0, 20};
        vibrator.vibrate(pattern, -1);
    }

}