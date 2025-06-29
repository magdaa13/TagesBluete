package at.fhj.tagesbluete;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Diese Activity ermöglicht es dem Benutzer, einen Notfallkontakt zu speichern und zu verwalten.
 * Der Kontakt wird in den SharedPreferences gespeichert und kann bei Bedarf bearbeitet werden.
 * Zusätzlich wird eine Vorschau der automatisierten Notfallnachricht angezeigt.
 */

public class Notfallkontakt extends AppCompatActivity {

    private EditText inputVorname;
    private EditText inputNachname;
    private EditText inputVerhaeltnis;
    private EditText inputTelefonnummer;
    private TextView textNachrichtvorschau;
    private SharedPreferences prefs;

    /**
     * Wird aufgerufen, wenn die Activity erstellt wird.
     * Initialisiert die Eingabefelder und lädt gespeicherte Daten, falls vorhanden.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notfallkontakt_verwalten);

        inputVorname = findViewById(R.id.input_vorname);
        inputNachname = findViewById(R.id.input_nachname);
        inputVerhaeltnis = findViewById(R.id.input_verhaeltnis);
        inputTelefonnummer = findViewById(R.id.input_telefonnummer);
        textNachrichtvorschau = findViewById(R.id.text_nachrichtvorschau);
        SharedPreferences globalPrefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String aktuellerNutzer = globalPrefs.getString("nutzername", "default");
        prefs = getSharedPreferences("NotfallPrefs_" + aktuellerNutzer, MODE_PRIVATE);


        ladeKontakt();

        findViewById(R.id.button_speichern).setOnClickListener(v -> {
            inputVorname.setEnabled(true);
            inputNachname.setEnabled(true);
            inputVerhaeltnis.setEnabled(true);
            inputTelefonnummer.setEnabled(true);

            speichereKontakt();
        });
        findViewById(R.id.button_bearbeiten).setOnClickListener(v -> {
            inputVorname.setEnabled(true);
            inputNachname.setEnabled(true);
            inputVerhaeltnis.setEnabled(true);
            inputTelefonnummer.setEnabled(true);
        });

    }

    /**
     * Lädt die gespeicherten Kontaktinformationen aus den SharedPreferences
     * und zeigt sie in den entsprechenden Feldern an.
     * Sperrt die Eingabefelder, wenn ein Kontakt bereits vorhanden ist.
     */
    private void ladeKontakt(){
        String vorname = prefs.getString("vorname", "");
        String nachname = prefs.getString("nachname", "");
        String verhaeltnis = prefs.getString("verhaeltnis", "");
        String nummer = prefs.getString("nummer", "");

        inputVorname.setText(vorname);
        inputNachname.setText(nachname);
        inputVerhaeltnis.setText(verhaeltnis);
        inputTelefonnummer.setText(nummer);

        aktualisiereNachrichtVorschau();

        boolean kontaktGespeichert = !vorname.isEmpty() || !nachname.isEmpty() || !verhaeltnis.isEmpty() || !nummer.isEmpty();

        inputVorname.setEnabled(!kontaktGespeichert);
        inputNachname.setEnabled(!kontaktGespeichert);
        inputVerhaeltnis.setEnabled(!kontaktGespeichert);
        inputTelefonnummer.setEnabled(!kontaktGespeichert);

        if (!kontaktGespeichert) {
            Toast.makeText(this, "Bitte trage deinen Notfallkontakt ein.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Speichert die aktuell eingegebenen Kontaktinformationen in den SharedPreferences
     * und deaktiviert die Bearbeitung der Felder.
     */
    private void speichereKontakt(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("vorname", inputVorname.getText().toString());
        editor.putString("nachname", inputNachname.getText().toString());
        editor.putString("verhaeltnis", inputVerhaeltnis.getText().toString());
        editor.putString("nummer", inputTelefonnummer.getText().toString());
        editor.apply();

        aktualisiereNachrichtVorschau();

        // Felder sperren
        inputVorname.setEnabled(false);
        inputNachname.setEnabled(false);
        inputVerhaeltnis.setEnabled(false);
        inputTelefonnummer.setEnabled(false);

        Toast.makeText(this,"Kontakt wurde gespeichert!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Aktualisiert die Vorschau der Notfallnachricht, die im Falle eines Sturzes gesendet werden könnte.
     */
    private void aktualisiereNachrichtVorschau(){
        String nachricht = "Achtung! Ich hatte möglicherweise einen Sturz. Bitte kontaktiere mich und überprüfe, ob ich Hilfe benötige.\n\n" +
                "Diese Nachricht wurde automatisch von der App TagesBlüte gesendet.\n\n" +
                "Standort: ";

        textNachrichtvorschau.setText(nachricht);
    }
}