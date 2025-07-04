package at.fhj.tagesbluete;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Das ist die Startübersicht der App.
 * Von hier aus gelangt man zu den Hauptfunktionen: Tagesplan, Garten, Notfallkontakt und Logout.
 * Zusätzlich wird das aktuelle Datum angezeigt.
 */

public class StartUebersicht extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_uebersicht);

        //Button Tagesplan öffnen
        Button buttonTagesplan = findViewById(R.id.button_tagesplan);
        buttonTagesplan.setOnClickListener(v -> {
            startActivity(new Intent(StartUebersicht.this, Tagesplan.class));
        });

        //Button Garten öffnen
        Button buttonMeinGarten = findViewById(R.id.button_meingarten);
        buttonMeinGarten.setOnClickListener(v -> {
            startActivity(new Intent(StartUebersicht.this, Garten.class));
        });

        //Button Notfallkontakte öffnen
        Button buttonNotfallkontakt = findViewById(R.id.button_notfallkontakte);
        buttonNotfallkontakt.setOnClickListener(v -> {
            startActivity(new Intent(StartUebersicht.this, Notfallkontakt.class));
        });

        //Button Abmelden
        Button buttonlogout = findViewById(R.id.button_logout);
        buttonlogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(StartUebersicht.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK); //nach Logout wird neue Task gestartet und alle vorherigen entfernt, damit man nicht per Zurück-Button zurückkommt
            startActivity(intent);
            finish(); //aktuelle Activity schließen
        });

        //Aktuelles Datum anzeigen
        TextView dateText = findViewById(R.id.text_date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d.M.yyyy", Locale.GERMAN); //formatiert ein Datum lesbar für den Menschen z.B. Montag, 1.1.2025 (Gebietsschema)
        String currentDate = sdf.format(new Date()); //holt aktuelles Datum und Uhrzeit
        dateText.setText(currentDate); //aktuelles Datum
    }
}
