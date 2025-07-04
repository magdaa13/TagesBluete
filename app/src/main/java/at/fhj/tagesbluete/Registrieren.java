package at.fhj.tagesbluete;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Die Registrieren-Activity erlaubt es neuen Benutzer:innen,
 * sich in der App zu registrieren. Dabei werden Benutzername und Passwort erfasst,
 * validiert und in der Datenbank gespeichert.
 *
 * <p>Nach erfolgreicher Registrierung wird gefragt, ob die Benutzer:in dauerhaft
 * eingeloggt bleiben möchte. Die Entscheidung wird in den
 * SharedPreferences gespeichert.</p>
 *
 */

public class Registrieren extends AppCompatActivity {

    /**
     * DAO für Datenbankzugriff auf Benutzer-Daten.
     */
    private BenutzerDAO benutzerDAO;

    /**
     * Wird aufgerufen, wenn die Activity erstellt wird.
     * Initialisiert UI-Komponenten und setzt die Logik für die Registrierung.
     *
     * @param savedInstanceState gespeicherter Zustand, kann null sein.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrieren);

        // Setzt korrekte Padding-Werte für das Layout bei Einblendung von Systemleisten
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_layout), ((v, insets) ->
        { Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        }));

        EditText benutzernameReg = findViewById(R.id.input_name);
        EditText passwortReg = findViewById(R.id.input_password);
        EditText passwortWhReg = findViewById(R.id.input_passwordRepeat);
        Button bestätigenReg = findViewById(R.id.button_register);

        benutzerDAO = RoomDatenbank.getInstance(getApplicationContext()).userDao();

        //Registrierung bei Klick auf Button starten
        bestätigenReg.setOnClickListener(v -> {
            String username = benutzernameReg.getText().toString().trim();
            String password = passwortReg.getText().toString().trim();
            String passwordRepeat = passwortWhReg.getText().toString().trim();

            //Validierung der Eingaben
            if(username.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()){
                Toast.makeText(Registrieren.this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(passwordRepeat)){
                Toast.makeText(Registrieren.this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
                return;
            }

            Benutzer existingBenutzer = benutzerDAO.findByUsername(username);
            if(existingBenutzer!=null) {
                Toast.makeText(Registrieren.this, "Benutzername bereits vergeben!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Benutzerobjekt erstellen und in DB speichern
            Benutzer neuerBenutzer = new Benutzer();
            neuerBenutzer.benutzername = username;
            neuerBenutzer.passwort = password;

            benutzerDAO.insert(neuerBenutzer);

            // Initiale Speicherung des Benutzernamens
            SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nutzername", username);
            editor.putBoolean("eingeloggt_bleiben", false);
            editor.apply();

            Toast.makeText(Registrieren.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();

            //Dialog "Eingeloggt bleiben?"
            runOnUiThread(() -> {
                new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                        .setTitle("Eingeloggt bleiben?")
                        .setMessage("Möchtest du dauerhaft eingeloggt bleiben? Das reduziert die Anzahl der Anmeldungen.")
                        .setPositiveButton("Ja", (dialog, which) -> {
                            SharedPreferences prefs1 = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = prefs1.edit();
                            editor1.putString("nutzername", username);
                            editor1.putBoolean("eingeloggt_bleiben", true);
                            editor1.apply();

                            Toast.makeText(Registrieren.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registrieren.this, StartUebersicht.class));
                            finish();
                        })
                        .setNegativeButton("Nein", (dialog, which) -> {
                            SharedPreferences prefs2 = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = prefs2.edit();
                            editor2.putString("nutzername", username);
                            editor2.putBoolean("eingeloggt_bleiben", false);
                            editor2.apply();

                            Toast.makeText(Registrieren.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registrieren.this, StartUebersicht.class));
                            finish();
                        })
                        .setCancelable(false) //Benutzer muss eine Option auswählen und kann den Dialog nicht schließen
                        .show();
            });


        });

    }
}
