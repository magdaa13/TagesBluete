package at.fhj.tagesbluete;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import android.Manifest;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;

public class Sturzerkennung extends AppCompatActivity {

    public Handler handler = new Handler();
    public Runnable sendAlertRunnable;
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int ABORT_WINDOW_MILLIS = 30000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sturzerkennung);

        sendAlertRunnable = this::sendEmergencySMS;

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            handler.removeCallbacks(sendAlertRunnable);
            Toast.makeText(this, "Alarm abgebrochen", Toast.LENGTH_SHORT).show();
            finish();
        });

        if (!hasPermissions()) {
            requestPermissions();
        } else {
            startCountdown();
        }
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSION_REQUEST_CODE);
    }

    private void startCountdown() {
        handler.postDelayed(sendAlertRunnable, ABORT_WINDOW_MILLIS); // 30 Sekunden Zeit zum Abbrechen
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasPermissions()) {
                startCountdown();
            } else {
                Toast.makeText(this, "Berechtigungen fehlen - SMS kann nicht gesendet werden.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void sendEmergencySMS() {

        SharedPreferences prefs = getSharedPreferences("NotfallPrefs", MODE_PRIVATE);

        String vorname = prefs.getString("vorname", "");
        String nachname = prefs.getString("nachname", "");
        String verhaeltnis = prefs.getString("verhaeltnis", "");
        String nummer = prefs.getString("nummer", "");

        if (nummer == null || nummer.isEmpty()) {
            Toast.makeText(this, "Keine Notfallnummer hinterlegt!", Toast.LENGTH_LONG).show();
            return;
        }

        String nachricht = "Achtung! Ich hatte möglicherweise einen Sturz. Bitte kontaktiere mich und überprüfe, ob ich Hilfe benötige.\n\n" +
                           "Diese Nachricht wurde automatisch von der App TagesBlüte gesendet.\n\n" +
                           "Standort: ";

        String standortText = "nicht verfügbar";
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = null;

        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (location == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (location != null) {
            standortText = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
        }

        String finalMessage = nachricht + standortText;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(nummer, null, finalMessage, null, null);
        }catch(Exception e){
            Toast.makeText(this, "Fehler beim Senden der SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    finish();
    }
}
