package at.fhj.tagesbluete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;


public class NeueAufgaben extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_neue_aufgaben);

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

        buttonHinzufügen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titel = TextTitel.getText().toString().trim();
                String datum = TextDatum.getText().toString().trim();
                String uhrzeit = TextUhrzeit.getText().toString().trim();
                String wiederholung = SpinnerWH.getSelectedItem().toString();

                if (titel.isEmpty() || datum.isEmpty() || uhrzeit.isEmpty()) {
                    Toast.makeText(NeueAufgaben.this, "Bitte alle Felder ausfüllen", Toast.LENGTH_SHORT).show();
                    return;
                }
                Aufgabe neueAufgabe = new Aufgabe();
                neueAufgabe.titel = titel;
                neueAufgabe.datum = datum;
                neueAufgabe.uhrzeit = uhrzeit;
                neueAufgabe.wiederholung = wiederholung;

                RoomDatenbank db = RoomDatenbank.getInstance(getApplicationContext());
                db.aufgabeDao().insert(neueAufgabe);

                Toast.makeText(NeueAufgaben.this, "Aufgabe wurde erfolgreich gespeichert!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}