package at.fhj.tagesbluete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.Calendar;
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

            if (aufgabeID != -1 && bearbeiteteAufgabe != null) {
                // BEARBEITUNG
                bearbeiteteAufgabe.titel = titel;
                bearbeiteteAufgabe.datum = datum;
                bearbeiteteAufgabe.uhrzeit = uhrzeit;
                bearbeiteteAufgabe.wiederholung = wiederholung;
                bearbeiteteAufgabe.nutzername = eingeloggterNutzername; //Nutzerzuordnung

                db.aufgabeDao().update(bearbeiteteAufgabe);
                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde aktualisiert!", Toast.LENGTH_SHORT).show();
            } else {
                // NEUANLAGE
                Aufgabe neueAufgabe = new Aufgabe();
                neueAufgabe.titel = titel;
                neueAufgabe.datum = datum;
                neueAufgabe.uhrzeit = uhrzeit;
                neueAufgabe.wiederholung = wiederholung;
                neueAufgabe.nutzername = eingeloggterNutzername;

                db.aufgabeDao().insert(neueAufgabe);
                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde gespeichert!", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }
}