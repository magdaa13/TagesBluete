package at.fhj.tagesbluete;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {
    private BenutzerDAO userDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText eingabeBenutzer = findViewById(R.id.input_usernameInput);
        EditText eingabePasswort = findViewById(R.id.input_passwordInput);
        Button loginBestätigen = findViewById(R.id.button_loginBestätigenButton);

        userDao = RoomDatenbank.getInstance(getApplicationContext()).userDao();

        loginBestätigen.setOnClickListener(v -> {
            String inputBenutzer = eingabeBenutzer.getText().toString();
            String inputPasswort = eingabePasswort.getText().toString();

            // Datenbankabfrage im Hintergrundthread
            executor.execute(() -> {
                Benutzer benutzer = userDao.login(inputBenutzer, inputPasswort);

                runOnUiThread(() -> {
                    if (benutzer != null) {
                        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("nutzername", benutzer.benutzername);
                        editor.apply();

                        new AlertDialog.Builder(Login.this)
                                .setTitle("Eingeloggt bleiben?")
                                .setMessage("Möchtest du dauerhaft eingeloggt bleiben? Das reduziert die Anzahl der Anmeldungen.")
                                        .setPositiveButton("Ja", (dialog, which) -> {
                                            SharedPreferences prefs1 = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = prefs1.edit();
                                            editor1.putString("nutzername", benutzer.benutzername);
                                            editor1.putBoolean("eingeloggt_bleiben", true);
                                            editor1.apply();

                                            Toast.makeText(Login.this, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, StartUebersicht.class));
                                            finish();
                                        })
                                        .setNegativeButton("Nein", (dialog, which) -> {
                                            SharedPreferences preferences = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor2 = preferences.edit();
                                            editor2.putString("nutzername", benutzer.benutzername);
                                            editor2.putBoolean("eingeloggt_bleiben", false);
                                            editor2.apply();

                                            Toast.makeText(Login.this, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, StartUebersicht.class));
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show();




                    } else {
                        Toast.makeText(Login.this, "Benutzername oder Passwort falsch", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();  // Executor-Service sauber schließen
    }
}
