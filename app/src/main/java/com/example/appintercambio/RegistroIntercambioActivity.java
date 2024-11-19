package com.example.appintercambio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.appintercambio.Models.Exchange;
import com.example.appintercambio.Models.Participant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistroIntercambioActivity  extends AppCompatActivity {
    private EditText editTextFecha;
    private EditText editTextHora;
    private Button buttonContinuar;
    private EditText editTextTematica;
    private EditText editTextLugar;
    private EditText editTextPrecioMinimo;
    private EditText editTextPrecioMaximo;

    //Firebase stuff
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private List<Participant> participants = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_intercambio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeViews();
        setupTextFields();
        setupDateField();
        setupTimeField();
        setupPriceFields();
        setupButtons();
    }

    private void initializeViews() {
        editTextFecha = findViewById(R.id.editTextFecha);
        editTextHora = findViewById(R.id.editTextHora);
        buttonContinuar = findViewById(R.id.buttonContinuar);
        editTextTematica = findViewById(R.id.editTextTematica);
        editTextLugar = findViewById(R.id.editTextLugar);
        editTextPrecioMinimo = findViewById(R.id.editTextPrecioMinimo);
        editTextPrecioMaximo = findViewById(R.id.editTextPrecioMaximo);
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
        editTextTematica.setOnFocusChangeListener(textFieldListener);
        editTextLugar.setOnFocusChangeListener(textFieldListener);

        // Agregar el listener para el botón Done
        editTextTematica.setOnEditorActionListener(editorActionListener);
        editTextLugar.setOnEditorActionListener(editorActionListener);
    }

    private void setupPriceFields() {
        View.OnFocusChangeListener priceFieldListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {  // Cuando el campo pierde el foco
                    EditText editText = (EditText) v;
                    String value = editText.getText().toString().replaceAll("[^\\d]", "");

                    if (!value.isEmpty()) {
                        try {
                            StringBuilder formatted = new StringBuilder();
                            int len = value.length();

                            for (int i = 0; i < len; i++) {
                                if (i > 0 && (len - i) % 3 == 0) {
                                    formatted.append(',');
                                }
                                formatted.append(value.charAt(i));
                            }

                            editText.setText(formatted.toString());
                        } catch (Exception e) {
                            Log.e("PriceFormat", "Error formatting price", e);
                        }
                    }
                }
            }
        };

        // Listener para el botón Realiz del teclado
        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus();  // Quita el foco del campo

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        };
        // Establecer los listeners
        editTextPrecioMinimo.setOnFocusChangeListener(priceFieldListener);
        editTextPrecioMaximo.setOnFocusChangeListener(priceFieldListener);

        editTextPrecioMinimo.setOnEditorActionListener(editorActionListener);
        editTextPrecioMaximo.setOnEditorActionListener(editorActionListener);
    }

    private void setupButtons() {
        //buttonContinuar.setBackgroundColor(getResources().getColor(R.color.nobutton));
        buttonContinuar.setOnClickListener(v -> {
            if(validateFields()) {
                printFieldsInfo();
                vibrate();
                insert();
                Intent intent = new Intent(this, RaffleActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        if(editTextLugar.getText().toString().trim().isEmpty()) {
            editTextLugar.setError("Campo requerido");
            isValid = false;
        }
        if(editTextFecha.getText().toString().trim().isEmpty()) {
            editTextFecha.setError("Campo requerido");
            isValid = false;
        }
        if(editTextHora.getText().toString().trim().isEmpty()) {
            editTextHora.setError("Campo requerido");
            isValid = false;
        }

        if(editTextPrecioMinimo.getText().toString().trim().isEmpty()) {
            editTextPrecioMinimo.setError("Campo requerido");
            isValid = false;
        }
        if(editTextPrecioMaximo.getText().toString().trim().isEmpty()) {
            editTextPrecioMaximo.setError("Campo requerido");
            isValid = false;
        }

        if (!editTextPrecioMinimo.getText().toString().trim().isEmpty() &&
                !editTextPrecioMaximo.getText().toString().trim().isEmpty()) {
            try {
                String minStr = editTextPrecioMinimo.getText().toString().replaceAll("[^\\d]", "");
                String maxStr = editTextPrecioMaximo.getText().toString().replaceAll("[^\\d]", "");
                int precioMin = Integer.parseInt(minStr);
                int precioMax = Integer.parseInt(maxStr);

                if (precioMin >= precioMax) {
                    editTextPrecioMinimo.setError("El precio mínimo debe ser menor al máximo");
                    editTextPrecioMaximo.setError("El precio máximo debe ser mayor al mínimo");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editTextPrecioMinimo.setError("Precio inválido");
                editTextPrecioMaximo.setError("Precio inválido");
                isValid = false;
            }
        }

        return isValid;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 0, 20, 0, 0, 20};
        vibrator.vibrate(pattern, -1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDateField() {
        editTextFecha.setOnClickListener(v -> {
            hideSoftKeyboard(v);
            showDatePicker();});
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTimeField() {
        editTextHora.setOnClickListener(v -> {
            hideSoftKeyboard(v);
            showTimePicker();
        });
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePicker() {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(requireContext(), this, year, month, day);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String yearStr = String.valueOf(year).substring(2);
            String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%s", day, month + 1, yearStr);
            ((EditText) requireActivity().findViewById(R.id.editTextFecha)).setText(selectedDate);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            String selectedTime = timeFormatter.format(calendar.getTime()).toLowerCase();

            ((EditText) requireActivity().findViewById(R.id.editTextHora)).setText(selectedTime);
        }
    }

    // Log para seguiminto de la informacion
    private void printFieldsInfo() {
        Log.d("INFO_CAMPOS", "=== Información de Campos ===");
        Log.d("INFO_CAMPOS", "Temática: " + editTextTematica.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Lugar: " + editTextLugar.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Fecha: " + editTextFecha.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Hora: " + editTextHora.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Precio Mínimo: " + editTextPrecioMinimo.getText().toString().trim());
        Log.d("INFO_CAMPOS", "Precio Máximo: " + editTextPrecioMaximo.getText().toString().trim());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void insert(){
        String theme = editTextTematica.getText().toString().trim();
        String date = editTextTematica.getText().toString().trim();
        String location = editTextLugar.getText().toString().trim();
        String time = editTextHora.getText().toString().trim();
        String minPriceString = editTextPrecioMinimo.getText().toString().trim();
        int minPrice = Integer.parseInt(minPriceString);
        String maxPriceString = editTextPrecioMinimo.getText().toString().trim();
        int maxPrice = Integer.parseInt(maxPriceString);

        Exchange exchange = new Exchange(1, theme, location, date, time, minPrice, maxPrice);

        DatabaseReference ref = db.getReference("evento/" + exchange.getId());

        ref.setValue(exchange).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Evento registrado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al registrar evento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}