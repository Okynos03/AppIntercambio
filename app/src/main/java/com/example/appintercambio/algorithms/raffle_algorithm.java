package com.example.appintercambio.algorithms;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.appintercambio.Models.Participant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class raffle_algorithm {
    private static final String TAG = "Sorteo";
    private FirebaseDatabase db;

    public raffle_algorithm() {
        db = FirebaseDatabase.getInstance();
    }

    public interface SorteoCallback {
        void onSorteoCompleto(Map<String, String> resultado);
        void onError(Exception e);
    }

    public void realizarSorteo(List<Participant> participantes, SorteoCallback callback) {
        List<String> nombres = new ArrayList<>();
        for(Participant p : participantes) {
            nombres.add(p.getName());
        }

        if(nombres.isEmpty()) {
            callback.onError(new Exception("No hay participantes para realizar el sorteo"));
            return;
        }

        Map<String, String> resultados = ejecutarSorteo(nombres);
        guardarResultadosSorteo(resultados, callback);
    }

    private Map<String, String> ejecutarSorteo(List<String> participantes) {
        List<String> emisor = new ArrayList<>(participantes);
        List<String> receptor = new ArrayList<>(participantes);
        Map<String, String> result = new LinkedHashMap<>();

        Random random = new Random();
        String first_e = "Nan";
        String e_selected = "nan";

        Log.d(TAG, "Iniciando sorteo con " + participantes.size() + " participantes");

        while (!emisor.isEmpty() && !receptor.isEmpty()) {
            if (first_e.equals("Nan")) {
                int rand = random.nextInt(emisor.size());
                e_selected = emisor.get(rand);
                first_e = e_selected;
                receptor.remove(first_e);
                Log.d(TAG, "Primera selección: " + first_e);
            }

            int new_rand = random.nextInt(receptor.size());
            String r_selected = receptor.get(new_rand);

            if (r_selected.equals(e_selected)) {
                continue;
            }

            emisor.remove(e_selected);
            receptor.remove(r_selected);
            result.put(e_selected, r_selected);
            Log.d(TAG, e_selected + " le dará regalo a " + r_selected);
            e_selected = r_selected;

            if (receptor.isEmpty()) {
                emisor.remove(e_selected);
                result.put(e_selected, first_e);
                Log.d(TAG, "Cerrando círculo: " + e_selected + " le dará regalo a " + first_e);
                break;
            }
        }

        return result;
    }

    private void guardarResultadosSorteo(Map<String, String> resultados, SorteoCallback callback) {
        DatabaseReference sorteoRef = db.getReference("sorteo");

        Map<String, Object> todosLosResultados = new LinkedHashMap<>();
        int i = 1;
        for (Map.Entry<String, String> entry : resultados.entrySet()) {
            Map<String, String> participacion = new HashMap<>();
            participacion.put("from", entry.getKey());
            participacion.put("to", entry.getValue());
            todosLosResultados.put(String.valueOf(i), participacion);
            i++;
        }

        sorteoRef.setValue(todosLosResultados).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Resultados guardados exitosamente");
                    callback.onSorteoCompleto(resultados);
                } else {
                    Log.e(TAG, "Error al guardar resultados", task.getException());
                    callback.onError(task.getException());
                }
            }
        });
    }
}