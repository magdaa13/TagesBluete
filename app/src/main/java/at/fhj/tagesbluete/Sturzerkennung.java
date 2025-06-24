package at.fhj.tagesbluete;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import android.Manifest;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;

public class Sturzerkennung extends AppCompatActivity {

    public Handler handler = new Handler();
    public Runnable sendAlertRunnable;
    public static final int PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sturzerkennung);

        sendAlertRunnable = this::sendEmergencySMS;

        if (!hasPermissions()) {
            requestPermissions();
        } else {
            startCountdown();
        }
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            handler.removeCallbacks(sendAlertRunnable);
            Toast.makeText(this, "Alarm abgebrochen", Toast.LENGTH_SHORT).show();
            finish();
        });

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
        handler.postDelayed(sendAlertRunnable, 10000); // 10 Sekunden Zeit zum Abbrechen
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasPermissions()) {
                startCountdown();
            } else {
                Toast.makeText(this, "Berechtigungen benötigt, um Notfall-SMS zu senden", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void sendEmergencySMS() {
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

        String message = "Möglicher Sturz erkannt! Bitte überprüfen. Standort: ";
        if (location != null) {
            message += "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
        } else {
            message += "Standort nicht verfügbar.";
        }

        String phoneNumber = "DEIN_NOTFALLKONTAKT"; // TODO: Kontakt hinterlegen / dynamisch laden

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);

        Toast.makeText(this, "Notfall-SMS gesendet", Toast.LENGTH_LONG).show();
        finish();
    }


}