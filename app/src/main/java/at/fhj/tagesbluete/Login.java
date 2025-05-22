package at.fhj.tagesbluete;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {
    public BenutzerDAO userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        EditText eingabeBenutzer = findViewById(R.id.usernameInput);
        EditText eingabePasswort = findViewById(R.id.passwordInput);
        Button loginBestätigen = findViewById(R.id.loginBestätigenButton);


        //Zugriff auf DAO
        userDao = RoomDatenbank.getInstance(getApplicationContext()).userDao();


        loginBestätigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputBenutzer = eingabeBenutzer.getText().toString();
                String inputPasswort = eingabePasswort.getText().toString();

                //benutzer aus der Datenbank suchen
                Benutzer benutzer = userDao.login(inputBenutzer, inputPasswort);

                if (benutzer != null) {
                    Toast.makeText(Login.this, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Benutzername oder Passwort falsch", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}