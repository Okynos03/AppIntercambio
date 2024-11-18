package com.example.appintercambio;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroParticipantesActivity  extends AppCompatActivity {
    private EditText editTextNombre;
    private EditText editTextCorreo;
    private Button buttonRegistrarParticipante;
    private Button buttonContinuarP;
    private Vibrator vibrator;


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

    private void setupTextFields() {
        // Listener para cuando pierden el foco
        View.OnFocusChangeListener textFieldListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Ocultar el teclado
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        };

        // Listener para el botón Done del teclado
        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus();  // Quita el foco del campo
                    // El teclado se ocultará automáticamente por el OnFocusChangeListener
                    return true;
                }
                return false;
            }
        };

        // Configurar los campos
        editTextNombre.setOnFocusChangeListener(textFieldListener);
        editTextCorreo.setOnFocusChangeListener(textFieldListener);

        // Agregar el listener para el botón Done
        editTextNombre.setOnEditorActionListener(editorActionListener);
        editTextCorreo.setOnEditorActionListener(editorActionListener);
    }

    private void setupValidations() {
        /*buttonRegistrarParticipante.setOnClickListener(v -> {
            *if(validateFields()) {
                // Aqui se pude usar para continuar a otra pantalla
            }
        });
        buttonContinuarP.setOnClickListener(v -> {
            *if(validateFields()) {
                // Aqui se pude usar para continuar a otra pantalla
            }
         */
    }

    // Log para seguiminto de la informacion
    private void printFieldsInfo() {
        Log.d("INFO_CAMPOS", "=== Información de Campos ===");
        Log.d("INFO_CAMPOS", "Temática: " + editTextNombre.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Lugar: " + editTextCorreo.getText().toString().trim());
    }

    private void setupVibrations() {
        buttonRegistrarParticipante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
            }
        });
        buttonContinuarP.setOnClickListener(v -> {
            buttonContinuarP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrate();
                }
            });
            printFieldsInfo();
        });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 0, 20, 0, 0, 20};
        vibrator.vibrate(pattern, -1);
    }

}