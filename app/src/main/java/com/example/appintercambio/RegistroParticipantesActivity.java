package com.example.appintercambio;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegistroParticipantesActivity  extends AppCompatActivity {

    private static int lastId = 0;

    private EditText editTextNombre;
    private EditText editTextCorreo;
    private Button buttonRegistrarParticipante;
    private Button buttonContinuarP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_participantes);
        initializeViews();
        setupButtons();
    }

    private void initializeViews() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        buttonRegistrarParticipante = findViewById(R.id.buttonRegistrarParticipante);
        buttonContinuarP = findViewById(R.id.buttonContinuarP);
    }

    private void setupButtons() {
        //buttonRegistrarParticipante.setEnabled(false);
        buttonRegistrarParticipante.setOnClickListener(v -> {
            vibrate();
            printFieldsInfo();
            RegParticipant();
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

    //Log para seguiminto de la informacion
    private void printFieldsInfo() {
        Log.d("INFO_CAMPOS", "Nombre: " + editTextNombre.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Correo: " + editTextCorreo.getText().toString().trim());
    }

    private void RegParticipant() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference participantRef = database.getReference("participante");

        String name = editTextNombre.getText().toString().trim();
        String email = editTextCorreo.getText().toString().trim();

        if (name.isEmpty() && email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre y un correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        /* //deshabilita el boton pero lo logro que funcione
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus) { // Se ejecuta cuando el campo pierde el foco
                    validateFields();
                }
            }
        };

        editTextNombre.setOnFocusChangeListener(focusChangeListener);
        editTextCorreo.setOnFocusChangeListener(focusChangeListener);

         */

        int idParticipant = createId();

        Map<String, Object> datosParticipante = new HashMap<>();
        datosParticipante.put("name", name);
        datosParticipante.put("email", email);

        participantRef.child(String.valueOf(idParticipant)).setValue(datosParticipante)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Participante registrado", Toast.LENGTH_SHORT).show();
                    editTextNombre.setText("");
                    editTextCorreo.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar participante", Toast.LENGTH_SHORT).show();
                    Log.e("FirebaseError", "Error al guardar participante: " + e.getMessage());
                });
    }

    private int createId(){
        lastId++;
        return lastId;
    }

    /*private void validateFields() {
        boolean validName = !editTextNombre.getText().toString().trim().isEmpty();
        boolean validEmail = !editTextCorreo.getText().toString().trim().isEmpty();
        buttonRegistrarParticipante.setEnabled(validName && validEmail);
    }
     */

}