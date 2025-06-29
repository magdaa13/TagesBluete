package at.fhj.tagesbluete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activity zur Erstellung und Bearbeitung von Aufgaben mit Datum, Uhrzeit und Wiederholungsoption.
 *
 * <p>Diese Activity ermöglicht dem Nutzer das Anlegen neuer Aufgaben oder das Bearbeiten bestehender Aufgaben.
 * Aufgaben können einen Titel, ein Datum,  eine Uhrzeit und eine Wiederholung (einmalig, täglich, wöchentlich) haben.</p>
 *
 * <p>Beim Speichern wird eine Benachrichtigung für die Aufgabe mit Hilfe von WorkManager geplant.
 * Für wiederkehrende Aufgaben wird ein periodischer WorkRequest erstellt, für einmalige Aufgaben ein OneTimeWorkRequest.</p>
 *
 * <p>Die Nutzerdaten werden aus SharedPreferences gelesen, um die Aufgabe einem Nutzer zuzuordnen.</p>
 *
 * <p>Datum und Uhrzeit werden über Dialoge zur Eingabe bereitgestellt.</p>
 */
public class NeueAufgaben extends AppCompatActivity {

    /**
     * ID der Aufgabe, die bearbeitet wird. -1 wenn keine Bearbeitung, sondern neue Aufgabe.
     */
    private int aufgabeID = -1;

    /**
     * Die Aufgabe, die gerade bearbeitet wird (null wenn neu).
     */
    private Aufgabe bearbeiteteAufgabe = null;

    /**
     * Methode, in der das Layout gesetzt und UI-Elemente initialisiert werden.
     *
     * <p>Hier werden Eingabefelder, Spinner und Buttons verbunden, Datum- und Zeit-Picker
     * geöffnet sowie bestehende Aufgaben geladen (Bearbeitungsmodus).</p>
     *
     * <p>Beim Klick auf den Speichern-Button werden die Eingaben überprüft, eine neue Aufgabe
     * gespeichert oder eine bestehende aktualisiert und eine passende WorkManager-Aufgabe
     * für Benachrichtigungen geplant.</p>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_neue_aufgaben);

        RoomDatenbank db = RoomDatenbank.getInstance(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String eingeloggterNutzername = prefs.getString("nutzername", "");

        EditText TextTitel = findViewById(R.id.input_titel);
        EditText TextDatum = findViewById(R.id.input_datum);
        EditText TextUhrzeit = findViewById(R.id.input_uhrzeit);
        Spinner SpinnerWH = findViewById(R.id.spinner_wiederholung);
        Button buttonHinzufügen = findViewById(R.id.button_hinzufuegen);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.wiederholungsoption,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerWH.setAdapter(adapter);

        // Öffnet DatePickerDialog bei Klick auf das Datum-Eingabefeld
        TextDatum.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int jahr = calendar.get(Calendar.YEAR);
            int monat = calendar.get(Calendar.MONTH);
            int tag = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(NeueAufgaben.this, (view, year, month, day) -> {
                String datumStr = String.format(Locale.GERMAN, "%02d.%02d.%04d", day, month + 1, year);
                TextDatum.setText(datumStr);
            }, jahr, monat, tag);
            datePickerDialog.show();
        });

        // Öffnet TimePickerDialog bei Klick auf das Uhrzeit-Eingabefeld
        TextUhrzeit.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int stunde = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(NeueAufgaben.this, (view, stunde1, minute1) -> {
                String uhrzeitStr = String.format(Locale.GERMAN, "%02d:%02d", stunde1, minute1);
                TextUhrzeit.setText(uhrzeitStr);
            }, stunde, minute, true);
            timePickerDialog.show();
        });

        // Prüfen, ob Bearbeitungsmodus (Aufgabe bearbeiten statt neu anlegen)
        aufgabeID = getIntent().getIntExtra("aufgabe_id", -1);
        if (aufgabeID != -1) {
            bearbeiteteAufgabe = db.aufgabeDao().findById(aufgabeID);
            if (bearbeiteteAufgabe != null) {
                TextTitel.setText(bearbeiteteAufgabe.titel);
                TextDatum.setText(bearbeiteteAufgabe.datum);
                TextUhrzeit.setText(bearbeiteteAufgabe.uhrzeit);
                int spinnerPosition = adapter.getPosition(bearbeiteteAufgabe.wiederholung);
                SpinnerWH.setSelection(spinnerPosition);
                buttonHinzufügen.setText(R.string.aenderung_speichern);
            }
        }

        // Listener für den Speichern-Button
        buttonHinzufügen.setOnClickListener(v -> {
            String titel = TextTitel.getText().toString().trim();
            String datum = TextDatum.getText().toString().trim();
            String uhrzeit = TextUhrzeit.getText().toString().trim();
            String wiederholung = SpinnerWH.getSelectedItem().toString();

            // Validierung der Eingaben
            if (titel.isEmpty() || datum.isEmpty() || uhrzeit.isEmpty()) {
                Toast.makeText(NeueAufgaben.this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            Aufgabe aktuelleAufgabe;
            if (aufgabeID != -1 && bearbeiteteAufgabe != null) {
                // Bearbeitung bestehender Aufgabe
                bearbeiteteAufgabe.titel = titel;
                bearbeiteteAufgabe.datum = datum;
                bearbeiteteAufgabe.uhrzeit = uhrzeit;
                bearbeiteteAufgabe.wiederholung = wiederholung;
                bearbeiteteAufgabe.nutzername = eingeloggterNutzername;

                db.aufgabeDao().update(bearbeiteteAufgabe);
                aktuelleAufgabe = bearbeiteteAufgabe;
                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde aktualisiert!", Toast.LENGTH_SHORT).show();
            } else {
                // Neue Aufgabe anlegen
                Aufgabe neueAufgabe = new Aufgabe();
                neueAufgabe.titel = titel;
                neueAufgabe.datum = datum;
                neueAufgabe.uhrzeit = uhrzeit;
                neueAufgabe.wiederholung = wiederholung;
                neueAufgabe.nutzername = eingeloggterNutzername;

                long neueId = db.aufgabeDao().insert(neueAufgabe);
                neueAufgabe.id = (int) neueId;
                aktuelleAufgabe = neueAufgabe;
                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde gespeichert!", Toast.LENGTH_SHORT).show();
            }

            // Planung der Benachrichtigung via WorkManager
            try {
                String datumUhrzeit = datum + " " + uhrzeit;
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
                Date date = sdf.parse(datumUhrzeit);
                assert date != null;
                long triggerMillis = date.getTime();

                WorkManager workManager = WorkManager.getInstance(getApplicationContext());

                Data inputData = new Data.Builder()
                        .putString("titel", titel)
                        .putInt("id", aktuelleAufgabe.id)
                        .build();

                long now = System.currentTimeMillis();
                long delay = triggerMillis - now;
                if (delay < 0) delay = 0;

                if (wiederholung.equals("Einmalig")) {
                    // Einmalige Benachrichtigung
                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AufgabenBenachrichtigung.class)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .build();

                    workManager.enqueue(workRequest);
                } else {
                    // Periodische Benachrichtigung (täglich oder wöchentlich)
                    long interval;
                    if (wiederholung.equals("Täglich")) {
                        interval = TimeUnit.DAYS.toMillis(1);
                    } else {
                        interval = TimeUnit.DAYS.toMillis(7);
                    }

                    PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                            AufgabenBenachrichtigung.class,
                            interval,
                            TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .build();

                    String uniqueWorkName = "aufgabe_" + aktuelleAufgabe.id;

                    workManager.enqueueUniquePeriodicWork(uniqueWorkName, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Activity beenden und zurückkehren
            finish();
        });
    }
}
