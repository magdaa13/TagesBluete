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

        if(eingeloggtBleiben){
            Intent intent = new Intent(MainActivity.this,StartUebersicht.class);
            startActivity(intent);
            finish();
            return;
        }

        // Berechtigung prüfen und Service starten
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_CODE);
            } else {
                startSensorService();
            }
        } else {
            startSensorService();
        }

        Button buttonLogin = findViewById(R.id.loginBestätigenButton);
        Button buttonRegistrieren = findViewById(R.id.registerButton);

        //Login Button
        buttonLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
        });

        //Registrieren Button
        buttonRegistrieren.setOnClickListener(v -> {
            Intent regIntent = new Intent(MainActivity.this, Registrieren.class);
            startActivity(regIntent);
        });
    }

    private void startSensorService() {
        Intent serviceIntent = new Intent(this, SensorService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSensorService();
            } else {
                Toast.makeText(this, "Berechtigung zur Aktivitätserkennung benötigt!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
