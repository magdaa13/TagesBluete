package at.fhj.tagesbluete;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class NeueAufgaben extends AppCompatActivity {

    int aufgabeID = -1;
    public Aufgabe bearbeiteteAufgabe = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_neue_aufgaben);
        RoomDatenbank db = RoomDatenbank.getInstance(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String eingeloggterNutzername = prefs.getString("nutzername", "");  // Fallback: leerer String


        EditText TextTitel = findViewById(R.id.editTextTitel);
        EditText TextDatum = findViewById(R.id.editTextDatum);
        EditText TextUhrzeit = findViewById(R.id.editTextUhrzeit);
        Spinner SpinnerWH = findViewById(R.id.spinnerWiederholung);
        Button buttonHinzufügen = findViewById(R.id.buttonHinzufuegen);

        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(
                this,
                R.array.wiederholungsoption,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerWH.setAdapter(adapter);

        TextDatum.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int jahr = calendar.get(Calendar.YEAR);
            int monat = calendar.get(Calendar.MONTH);
            int tag = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(NeueAufgaben.this, (view, year, month, day) -> {
                String datumStr = String.format(Locale.GERMAN,"%02d.%02d.%04d", day, month + 1, year);
                TextDatum.setText(datumStr);
            }, jahr, monat, tag);
            datePickerDialog.show();

        });

        TextUhrzeit.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int stunde = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(NeueAufgaben.this, (view, stunde1, minute1) -> {
                String uhrzeitStr = String.format(Locale.GERMAN, "%02d:%02d", stunde1, minute1);
                TextUhrzeit.setText(uhrzeitStr);
            }, stunde, minute, true); // true für 24-Stunden-Format
            timePickerDialog.show();

        });

        // Prüfen ob Bearbeitungsmodus
        aufgabeID = getIntent().getIntExtra("aufgabe_id", -1);
        if (aufgabeID != -1) {
            bearbeiteteAufgabe = db.aufgabeDao().findById(aufgabeID);
            if (bearbeiteteAufgabe != null) {
                TextTitel.setText(bearbeiteteAufgabe.titel);
                TextDatum.setText(bearbeiteteAufgabe.datum);
                TextUhrzeit.setText(bearbeiteteAufgabe.uhrzeit);
                int spinnerPosition = adapter.getPosition(bearbeiteteAufgabe.wiederholung);
                SpinnerWH.setSelection(spinnerPosition);
                buttonHinzufügen.setText("Änderung speichern");
            }
        }

        buttonHinzufügen.setOnClickListener(v -> {
            String titel = TextTitel.getText().toString().trim();
            String datum = TextDatum.getText().toString().trim();
            String uhrzeit = TextUhrzeit.getText().toString().trim();
            String wiederholung = SpinnerWH.getSelectedItem().toString();

            if (titel.isEmpty() || datum.isEmpty() || uhrzeit.isEmpty()) {
                Toast.makeText(NeueAufgaben.this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            Aufgabe aktuelleAufgabe;
            if (aufgabeID != -1 && bearbeiteteAufgabe != null) {
                // BEARBEITUNG
                bearbeiteteAufgabe.titel = titel;
                bearbeiteteAufgabe.datum = datum;
                bearbeiteteAufgabe.uhrzeit = uhrzeit;
                bearbeiteteAufgabe.wiederholung = wiederholung;
                bearbeiteteAufgabe.nutzername = eingeloggterNutzername; //Nutzerzuordnung

                db.aufgabeDao().update(bearbeiteteAufgabe);
                aktuelleAufgabe = bearbeiteteAufgabe;
                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde aktualisiert!", Toast.LENGTH_SHORT).show();
            } else {
                // Neu anlegen
                Aufgabe neueAufgabe = new Aufgabe();
                neueAufgabe.titel = titel;
                neueAufgabe.datum = datum;
                neueAufgabe.uhrzeit = uhrzeit;
                neueAufgabe.wiederholung = wiederholung;
                neueAufgabe.nutzername = eingeloggterNutzername;

               long neueId = db.aufgabeDao().insert(neueAufgabe);
               neueAufgabe.id = (int)neueId;
               aktuelleAufgabe = neueAufgabe;
                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde gespeichert!", Toast.LENGTH_SHORT).show();
            }

            try {
                String datumUhrzeit = datum + " " + uhrzeit;
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);
                Date date = sdf.parse(datumUhrzeit);
                long triggerMillis = date.getTime();

                Intent intent = new Intent(NeueAufgaben.this, AufgabenBenachrichtigung.class);
                intent.putExtra("titel", titel);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        NeueAufgaben.this,
                        aktuelleAufgabe.id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    switch(wiederholung){
                        case "Einmalig":
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent);
                            break;
                        case "Täglich":
                            alarmManager.setRepeating(
                                    AlarmManager.RTC_WAKEUP,triggerMillis,AlarmManager.INTERVAL_DAY,pendingIntent);
                            break;
                        case "Wöchentlich":
                            alarmManager.setRepeating(
                                    AlarmManager.RTC_WAKEUP,triggerMillis,AlarmManager.INTERVAL_DAY * 7, pendingIntent
                            );
                            break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        });
    }
}