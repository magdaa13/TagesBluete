package at.fhj.tagesbluete;

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
    public BenutzerDAO userDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText eingabeBenutzer = findViewById(R.id.usernameInput);
        EditText eingabePasswort = findViewById(R.id.passwordInput);
        Button loginBestätigen = findViewById(R.id.loginBestätigenButton);

        userDao = RoomDatenbank.getInstance(getApplicationContext()).userDao();

        loginBestätigen.setOnClickListener(v -> {
            String inputBenutzer = eingabeBenutzer.getText().toString();
            String inputPasswort = eingabePasswort.getText().toString();

            // Datenbankabfrage im Hintergrundthread
            executor.execute(() -> {
                Benutzer benutzer = userDao.login(inputBenutzer, inputPasswort);

                runOnUiThread(() -> {
                    if (benutzer != null) {
                        // SharedPreferences speichern
                        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("nutzername", benutzer.benutzername);
                        editor.apply();

                        Toast.makeText(Login.this, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, StartUebersicht.class);
                        startActivity(intent);
                        finish();
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
