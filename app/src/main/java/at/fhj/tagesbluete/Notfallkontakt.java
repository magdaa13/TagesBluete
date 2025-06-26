package at.fhj.tagesbluete;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Notfallkontakt extends AppCompatActivity {

    public EditText input_vorname;
    public EditText input_nachname;
    public EditText input_verhaeltnis;
    public EditText input_telefonnummer;
    public TextView text_nachrichtvorschau;
    public SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notfallkontakt_verwalten);

        input_vorname = findViewById(R.id.input_vorname);
        input_nachname = findViewById(R.id.input_nachname);
        input_verhaeltnis = findViewById(R.id.input_verhaeltnis);
        input_telefonnummer = findViewById(R.id.input_telefonnummer);
        text_nachrichtvorschau = findViewById(R.id.text_nachrichtvorschau);
        prefs = getSharedPreferences("NotfallPrefs",MODE_PRIVATE);

        ladeKontakt();

        findViewById(R.id.button_speichern).setOnClickListener(v -> {
            input_vorname.setEnabled(true);
            input_nachname.setEnabled(true);
            input_verhaeltnis.setEnabled(true);
            input_telefonnummer.setEnabled(true);

            speichereKontakt();
        });
        findViewById(R.id.button_bearbeiten).setOnClickListener(v -> {
            input_vorname.setEnabled(true);
            input_nachname.setEnabled(true);
            input_verhaeltnis.setEnabled(true);
            input_telefonnummer.setEnabled(true);
        });

    }
    public void ladeKontakt(){
        String vorname = prefs.getString("vorname", "");
        String nachname = prefs.getString("nachname", "");
        String verhaeltnis = prefs.getString("verhaeltnis", "");
        String nummer = prefs.getString("nummer", "");

        input_vorname.setText(vorname);
        input_nachname.setText(nachname);
        input_verhaeltnis.setText(verhaeltnis);
        input_telefonnummer.setText(nummer);

        aktualisiereNachrichtVorschau();

        boolean kontaktGespeichert = !vorname.isEmpty() || !nachname.isEmpty() || !verhaeltnis.isEmpty() || !nummer.isEmpty();

        input_vorname.setEnabled(!kontaktGespeichert);
        input_nachname.setEnabled(!kontaktGespeichert);
        input_verhaeltnis.setEnabled(!kontaktGespeichert);
        input_telefonnummer.setEnabled(!kontaktGespeichert);

        if (!kontaktGespeichert) {
            Toast.makeText(this, "Bitte trage deinen Notfallkontakt ein.", Toast.LENGTH_LONG).show();
        }
    }
    public void speichereKontakt(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("vorname", input_vorname.getText().toString());
        editor.putString("nachname", input_nachname.getText().toString());
        editor.putString("verhaeltnis", input_verhaeltnis.getText().toString());
        editor.putString("nummer", input_telefonnummer.getText().toString());
        editor.apply();

        aktualisiereNachrichtVorschau();

        // Felder sperren
        input_vorname.setEnabled(false);
        input_nachname.setEnabled(false);
        input_verhaeltnis.setEnabled(false);
        input_telefonnummer.setEnabled(false);

        Toast.makeText(this,"Kontakt wurde gespeichert!", Toast.LENGTH_SHORT).show();
    }

    public void aktualisiereNachrichtVorschau(){
        String nachricht = "Achtung! Ich hatte möglicherweise einen Sturz. Bitte kontaktiere mich und überprüfe, ob ich Hilfe benötige.\n\n" +
                "Diese Nachricht wurde automatisch von der App TagesBlüte gesendet.\n\n" +
                "Standort: ";

        text_nachrichtvorschau.setText(nachricht);
    }
}