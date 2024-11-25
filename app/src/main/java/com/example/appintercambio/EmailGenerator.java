package com.example.appintercambio;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.appintercambio.Models.Exchange;
import com.example.appintercambio.Models.Participant;
import com.example.appintercambio.Models.Raffle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailGenerator {
    private FirebaseDatabase db;
    private Exchange eventInfo;
    private Map<String, Participant> participantsMap;
    private List<Raffle> raffleList;
    private List<EmailData> emailDataList;

    public EmailGenerator() {
        db = FirebaseDatabase.getInstance();
        participantsMap = new HashMap<>();
        raffleList = new ArrayList<>();
        emailDataList = new ArrayList<>();
    }

    // Clase interna para almacenar los datos del email
    public static class EmailData {  // Cambiar a public static
        private String toEmail;
        private String emailContent;

        public EmailData(String toEmail, String emailContent) {
            this.toEmail = toEmail;
            this.emailContent = emailContent;
        }

        public String getToEmail() {
            return toEmail;
        }

        public String getEmailContent() {
            return emailContent;
        }
    }

    public void generateEmails(final EmailGenerationCallback callback) {
        // Primero obtenemos la información del evento
        loadEventInfo(new DatabaseCallback() {
            @Override
            public void onComplete() {
                // Después cargamos los participantes
                loadParticipants(new DatabaseCallback() {
                    @Override
                    public void onComplete() {
                        // Finalmente cargamos el sorteo y generamos los emails
                        loadRaffleAndGenerateEmails(callback);
                    }
                });
            }
        });
    }

    private void loadEventInfo(final DatabaseCallback callback) {
        DatabaseReference ref = db.getReference("evento");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot item : snapshot.getChildren()) {
                    eventInfo = item.getValue(Exchange.class);
                    break; // Asumiendo que solo hay un evento
                }
            } else {
                Log.e("EmailGenerator", "Error loading event info", task.getException());
            }
            callback.onComplete();
        });
    }

    private void loadParticipants(final DatabaseCallback callback) {
        DatabaseReference ref = db.getReference("participante");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Participant participant = item.getValue(Participant.class);
                    if (participant != null) {
                        participantsMap.put(participant.getName(), participant);
                    }
                }
            } else {
                Log.e("EmailGenerator", "Error loading participants", task.getException());
            }
            callback.onComplete();
        });
    }

    private void loadRaffleAndGenerateEmails(final EmailGenerationCallback callback) {
        DatabaseReference ref = db.getReference("sorteo");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Raffle raffle = item.getValue(Raffle.class);
                    if (raffle != null) {
                        raffleList.add(raffle);
                        generateEmailContent(raffle);
                    }
                }
                callback.onEmailsGenerated(emailDataList);
            } else {
                Log.e("EmailGenerator", "Error loading raffle", task.getException());
                callback.onEmailsGenerated(null);
            }
        });
    }

    private void generateEmailContent(Raffle raffle) {
        Participant fromParticipant = participantsMap.get(raffle.getFrom());
        if (fromParticipant == null) return;

        String emailContent = String.format(
                "Hola %s,\n\n" +
                        "Te informamos que en el intercambio navideño te tocó regalarle a %s.\n\n" +
                        "Detalles del intercambio:\n" +
                        "* Fecha: %s\n" +
                        "* Hora: %s\n" +
                        "* Lugar: %s\n" +
                        "* Temática: %s\n" +
                        "* Precio mínimo: $%d\n" +
                        "* Precio máximo: $%d\n",
                raffle.getFrom(),
                raffle.getTo(),
                eventInfo.getDate(),
                eventInfo.getTime(),
                eventInfo.getLocation(),
                eventInfo.getTheme(),
                eventInfo.getMin_price(),
                eventInfo.getMax_price()
        );

        emailDataList.add(new EmailData(fromParticipant.getEmail(), emailContent));
    }

    // Interfaces para callbacks
    public interface DatabaseCallback {
        void onComplete();
    }

    public interface EmailGenerationCallback {
        void onEmailsGenerated(List<EmailData> emailDataList);
    }
}