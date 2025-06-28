package at.fhj.tagesbluete;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Haupt-Activity der App "TagesBlüte".
 *
 * <p>Diese Activity ist der Einstiegspunkt der App. Sie prüft,
 * ob der Nutzer bereits eingeloggt ist und leitet ggf. weiter.
 * Außerdem werden notwendige Berechtigungen für SMS, Standort,
 * Activity Recognition und Benachrichtigungen abgefragt.</p>
 *
 * <p>Ist der Nutzer nicht eingeloggt, werden Buttons zum Login
 * und zur Registrierung angezeigt.</p>
 *
 * <p>Wenn alle benötigten Berechtigungen vorhanden sind,
 * wird der SensorService gestartet.</p>
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    /**
     * Initialisiert die Activity, prüft Login-Status und Berechtigungen,
     * und setzt die UI-Komponenten.
     *
     * @param savedInstanceState Zustand, falls vorhanden
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Padding für Systemleisten setzen (Edge-to-Edge Design)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Prüfen, ob Nutzer eingeloggt bleiben möchte
        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
        boolean eingeloggtBleiben = prefs.getBoolean("eingeloggt_bleiben", false);

        if (eingeloggtBleiben) {
            // Direkt zur Startübersicht weiterleiten
            Intent intent = new Intent(MainActivity.this, StartUebersicht.class);
            startActivity(intent);
            finish();
            return;
        }

        // Berechtigungen prüfen und ggf. anfragen
        if (hasRequiredPermissions()) {
            startSensorService();
        } else {
            requestPermissions();
        }

        // UI-Buttons für Login und Registrierung initialisieren
        Button buttonLogin = findViewById(R.id.button_loginBestätigen);
        Button buttonRegistrieren = findViewById(R.id.button_register);

        buttonLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
        });

        buttonRegistrieren.setOnClickListener(v -> {
            Intent regIntent = new Intent(MainActivity.this, Registrieren.class);
            startActivity(regIntent);
        });
    }

    /**
     * Prüft, ob alle erforderlichen Berechtigungen erteilt wurden.
     *
     * @return true, wenn alle benötigten Berechtigungen vorhanden sind, sonst false
     */
    private boolean hasRequiredPermissions() {
        boolean smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean activityRecognition = true; // Für Android < 10 standardmäßig true
        boolean postNotifications = true;  // Für Android < 13 standardmäßig true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activityRecognition = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            postNotifications = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }

        return smsPermission && locationPermission && activityRecognition && postNotifications;
    }

    /**
     * Fordert die noch nicht erteilten Berechtigungen vom Nutzer an.
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: SMS, Location, Activity Recognition, Notifications
            requestPermissions(new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.POST_NOTIFICATIONS
            }, PERMISSION_REQUEST_CODE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+: SMS, Location, Activity Recognition
            requestPermissions(new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
            }, PERMISSION_REQUEST_CODE);
        } else {
            // Unter Android 10: SMS, Location
            requestPermissions(new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Startet den SensorService als Foreground Service.
     */
    private void startSensorService() {
        Intent serviceIntent = new Intent(this, SensorService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * Callback für die Ergebnisbehandlung der Berechtigungsanfrage.
     * Startet den SensorService, wenn alle Berechtigungen erteilt wurden,
     * zeigt ansonsten eine Fehlermeldung an.
     *
     * @param requestCode Code der Anfrage
     * @param permissions Angefragte Berechtigungen
     * @param grantResults Ergebnisse der Berechtigungsanfrage
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startSensorService();
            } else {
                Toast.makeText(this, "Alle Berechtigungen werden benötigt!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
