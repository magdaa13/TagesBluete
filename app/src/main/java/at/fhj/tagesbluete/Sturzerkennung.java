package at.fhj.tagesbluete;

import androidx.annotation.NonNull;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Hier zur Erkennung von Stürzen.
 * Nach dem Aufruf wird ein 30-Sekunden-Countdown gestartet, in dem der Nutzer eine Notruf-SMS abbrechen kann.
 * Wird der Countdown nicht abgebrochen, wird automatisch eine SMS mit dem aktuellen Standort
 * an die gespeicherte Notfallkontaktperson gesendet.
 */

public class Sturzerkennung extends AppCompatActivity {

    /** Handler für den zeitverzögerten SMS-Versand */
    private final Handler handler = new Handler();

    /** Runnable, das den Notruf (SMS) auslöst */
    private Runnable sendAlertRunnable;

    /** Anfragecode für Berechtigungen */
    private static final int PERMISSION_REQUEST_CODE = 100;

    /** Zeitfenster in Millisekunden, in dem der Alarm abgebrochen werden kann (30 Sekunden) */
    private static final int ABORT_WINDOW_MILLIS = 30000;


    /**
     * Initialisiert die Activity, startet bei vorhandenen Berechtigungen den Countdown,
     * oder fordert sie andernfalls an.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sturzerkennung);

        sendAlertRunnable = this::sendEmergencySMS; //Kurzschreibweise für Runnable

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

    /**
     * Prüft, ob SMS- und Standort-Berechtigungen erteilt wurden.
     *
     * @return true, wenn beide Berechtigungen vorhanden sind
     */
    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Fordert die erforderlichen Berechtigungen (SMS,Standort) an.
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSION_REQUEST_CODE);
    }

    /**
     * Startet den 30-sekündigen Countdown bis zum automatischen SMS-Versand.
     */
    private void startCountdown() {
        Log.d("Sturzerkennung", "Countdown gestartet, SMS wird in 30 Sekunden gesendet wenn nicht abgebrochen");
        handler.removeCallbacks(sendAlertRunnable);
        handler.postDelayed(() -> {
            Log.d("Sturzerkennung", "Timeout erreicht, sende SMS");
            sendEmergencySMS();
        }, ABORT_WINDOW_MILLIS);
    }

    /**
     * Wird aufgerufen, wenn der Benutzer die Berechtigungsabfrage beantwortet.
     */
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

    /**
     * Sendet eine Notfall-SMS mit aktuellem Standort an die hinterlegte Notfallnummer.
     * Die Daten werden aus SharedPreferences geladen.
     * Wenn keine Notfallnummer vorhanden ist oder ein Fehler auftritt, wird dies als Toast angezeigt.
     */
    @SuppressLint("MissingPermission")
    private void sendEmergencySMS() {
        SharedPreferences globalPrefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String aktuellerNutzer = globalPrefs.getString("nutzername", "default");
        SharedPreferences prefs = getSharedPreferences("NotfallPrefs_" + aktuellerNutzer, MODE_PRIVATE);

        String nummer = prefs.getString("nummer", "");

        if (nummer == null || nummer.isEmpty()) {
            Toast.makeText(this, "Keine Notfallnummer hinterlegt!", Toast.LENGTH_LONG).show();
            return;
        }

        String nachricht = "Achtung! Ich hatte möglicherweise einen Sturz. Bitte kontaktiere mich und überprüfe, ob ich Hilfe benötige.\n\n" +
                "Diese Nachricht wurde automatisch von der App TagesBlüte gesendet.\n\n" +
                "Standort: ";

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); //ruft Standortinformationen vom Gerät ab und liefert genauen Standort aus mehreren Quellen

        Log.d("Sturzerkennung", "Versuche getCurrentLocation()...");

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d("Sturzerkennung", "getCurrentLocation erfolgreich: " + location.getLatitude() + ", " + location.getLongitude()); //zur Überprüfung
                        String standortText = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        sendSms(nummer, nachricht + standortText);
                    } else {
                        Log.w("Sturzerkennung", "getCurrentLocation war null, versuche getLastLocation...");
                        fusedLocationProviderClient.getLastLocation()
                                .addOnSuccessListener(lastLocation -> {
                                    if (lastLocation != null) {
                                        Log.d("Sturzerkennung", "getLastLocation erfolgreich: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                                        String standortText = "https://maps.google.com/?q=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                                        sendSms(nummer, nachricht + standortText);
                                    } else {
                                        sendSms(nummer, nachricht + "nicht verfügbar");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Sturzerkennung", "getLastLocation fehlgeschlagen", e);
                                    sendSms(nummer, nachricht + "nicht verfügbar");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Sturzerkennung", "getCurrentLocation fehlgeschlagen", e);
                    sendSms(nummer, nachricht + "nicht verfügbar");
                });
    }

    /**
     * Sendet eine SMS mit dem übergebenen Nachrichtentext an die angegebene Telefonnummer.
     * Bei Erfolg wird eine Bestätigung angezeigt. Bei Fehlern wird eine Fehlermeldung geloggt und angezeigt.
     *
     * @param nummer ist die Telefonnummer des Empfängers.
     * @param nachricht ist der zu sendende Nachrichtentext, inkl. ggf. Standortinformation.
     */
    private void sendSms(String nummer, String nachricht) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(nummer, null, nachricht, null, null);
            Toast.makeText(this, "Notfall-SMS gesendet!", Toast.LENGTH_LONG).show();
            Log.d("Sturzerkennung", "SMS gesendet an " + nummer + ": " + nachricht);
        } catch (Exception e) {
            Toast.makeText(this, "Fehler beim SMS-Versand: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Sturzerkennung", "SMS-Senden fehlgeschlagen", e);
        }
        finish();
    }

}