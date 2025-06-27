package at.fhj.tagesbluete;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Registrieren extends AppCompatActivity {

    private BenutzerDAO benutzerDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrieren);
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

        bestätigenReg.setOnClickListener(v -> {
            String username = benutzernameReg.getText().toString().trim();
            String password = passwortReg.getText().toString().trim();
            String passwordRepeat = passwortWhReg.getText().toString().trim();

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

            Benutzer neuerBenutzer = new Benutzer();
            neuerBenutzer.benutzername = username;
            neuerBenutzer.passwort = password;

            benutzerDAO.insert(neuerBenutzer);

            SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nutzername", username);
            editor.putBoolean("eingeloggt_bleiben", false);
            editor.apply();

            Toast.makeText(Registrieren.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();

            runOnUiThread(() -> {
                new AlertDialog.Builder(Registrieren.this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
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
                        .setCancelable(false)
                        .show();
            });


        });

    }
}
