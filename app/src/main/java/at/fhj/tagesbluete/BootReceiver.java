package at.fhj.tagesbluete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

/**
 * BroadcastReceiver, der beim Systemstart ausgelöst wird.
 * Wenn das Gerät neu gestartet wird und der Bootvorgang abgeschlossen ist,
 * startet dieser Receiver den SensorService im Vordergrundmodus,
 * um z.B. Sensorüberwachung oder Erinnerungsfunktionen wieder zu aktivieren.
 */

public class BootReceiver extends BroadcastReceiver {

    /**
     * Wird aufgerufen, wenn der Broadcast empfangen wird.
     *
     * @param context der Kontext, in dem der Receiver ausgeführt wird
     * @param intent das empfangene Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, SensorService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}
