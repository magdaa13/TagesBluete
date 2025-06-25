package at.fhj.tagesbluete;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Notfallkontakt extends AppCompatActivity {

    public EditText etVorname;
    public EditText etNachname;
    public EditText etVerhaeltnis;
    public EditText etTelefonnummer;
    public TextView tvNachrichtVorschau;
    public SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notfallkontakt_verwalten);

        etVorname = findViewById(R.id.etVorname);
        etNachname = findViewById(R.id.etNachname);
        etVerhaeltnis = findViewById(R.id.etVerhaeltnis);
        etTelefonnummer = findViewById(R.id.etTelefonnummer);
        tvNachrichtVorschau = findViewById(R.id.tvNachrichtVorschau);
        prefs = getSharedPreferences("NotfallPrefs",MODE_PRIVATE);

        ladeKontakt();

        findViewById(R.id.btnSpeichern).setOnClickListener(v -> {
            etVorname.setEnabled(true);
            etNachname.setEnabled(true);
            etVerhaeltnis.setEnabled(true);
            etTelefonnummer.setEnabled(true);

            speichereKontakt();
        });
        findViewById(R.id.btnBearbeiten).setOnClickListener(v -> {
            etVorname.setEnabled(true);
            etNachname.setEnabled(true);
            etVerhaeltnis.setEnabled(true);
            etTelefonnummer.setEnabled(true);
        });

    }
    public void ladeKontakt(){
        String vorname = prefs.getString("vorname", "");
        String nachname = prefs.getString("nachname", "");
        String verhaeltnis = prefs.getString("verhaeltnis", "");
        String nummer = prefs.getString("nummer", "");

        etVorname.setText(vorname);
        etNachname.setText(nachname);
        etVerhaeltnis.setText(verhaeltnis);
        etTelefonnummer.setText(nummer);

        aktualisiereNachrichtVorschau();

        boolean kontaktGespeichert = !vorname.isEmpty() || !nachname.isEmpty() || !verhaeltnis.isEmpty() || !nummer.isEmpty();

        etVorname.setEnabled(!kontaktGespeichert);
        etNachname.setEnabled(!kontaktGespeichert);
        etVerhaeltnis.setEnabled(!kontaktGespeichert);
        etTelefonnummer.setEnabled(!kontaktGespeichert);

        if (!kontaktGespeichert) {
            Toast.makeText(this, "Bitte trage deinen Notfallkontakt ein.", Toast.LENGTH_LONG).show();
        }
    }
    public void speichereKontakt(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("vorname", etVorname.getText().toString());
        editor.putString("nachname", etNachname.getText().toString());
        editor.putString("verhaeltnis", etVerhaeltnis.getText().toString());
        editor.putString("nummer", etTelefonnummer.getText().toString());
        editor.apply();

        aktualisiereNachrichtVorschau();

        // Felder sperren
        etVorname.setEnabled(false);
        etNachname.setEnabled(false);
        etVerhaeltnis.setEnabled(false);
        etTelefonnummer.setEnabled(false);

        Toast.makeText(this,"Kontakt wurde gespeichert!", Toast.LENGTH_SHORT).show();
    }

    public void aktualisiereNachrichtVorschau(){
        String nachricht = "Achtung! Ich hatte möglicherweise einen Sturz. Bitte kontaktiere mich und überprüfe, ob ich Hilfe benötige.\n\n" +
                "Diese Nachricht wurde automatisch von der App TagesBlüte gesendet.\n\n" +
                "Standort: ";

        tvNachrichtVorschau.setText(nachricht);
    }
}