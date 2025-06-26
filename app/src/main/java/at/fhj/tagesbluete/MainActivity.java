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

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
        boolean eingeloggtBleiben = prefs.getBoolean("eingeloggt_bleiben", false);

        if (eingeloggtBleiben) {
            Intent intent = new Intent(MainActivity.this, StartUebersicht.class);
            startActivity(intent);
            finish();
            return;
        }

        if (hasRequiredPermissions()) {
            startSensorService();
        } else {
            requestPermissions();
        }

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

    private boolean hasRequiredPermissions() {
        boolean smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean activityRecognition = true; // Standard true für < Android 10
        boolean postNotifications = true; // Standard true für < Android 13

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activityRecognition = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            postNotifications = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }

        return smsPermission && locationPermission && activityRecognition && postNotifications;
    }

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

    private void startSensorService() {
        Intent serviceIntent = new Intent(this, SensorService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

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
