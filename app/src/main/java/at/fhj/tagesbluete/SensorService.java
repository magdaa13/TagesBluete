package at.fhj.tagesbluete;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
/**
 * Service zur Sturzerkennung und Bewegungserinnerung.
 * Läuft im Vordergrund mit Benachrichtigungen.
 */
public class SensorService extends Service implements SensorEventListener {

    /**
     * Verwalter für Zugriff auf Sensoren.
     */
    private SensorManager sensorManager;

    /**
     * Zeitstempel der letzten erkannten Bewegung (in Millisekunden seit Systemstart).
     */
    private long lastMovementTime = System.currentTimeMillis();

    /**
     * Zeitspanne in Millisekunden, nach der bei Inaktivität eine Erinnerung gesendet wird.
     * Aktuell: 3 Stunden.
     */
    private static final long INACTIVITY_THRESHOLD = 1000 * 60 * 60 * 3; // 3 Stunden keine Aktivität

    /**
     * Handler zur Überprüfung der Inaktivität im Haupt-Thread.
     */
    private final Handler inactivityHandler = new Handler(Looper.getMainLooper());

    /**
     * ID des Benachrichtigungskanals für den Foreground-Service.
     */
    private static final String CHANNEL_ID = "SturzerkennungsServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, buildForegroundNotification());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (motionSensor != null) {
            sensorManager.registerListener(this, motionSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        inactivityHandler.post(inactivityCheckRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Service soll nach Beendigung neu starten
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        inactivityHandler.removeCallbacks(inactivityCheckRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectFall(event);
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            detectInactivity(event);
        }
    }

    /**
     * Flag zur Vermeidung mehrfacher Benachrichtigungen bei Inaktivität.
     */
    private boolean inactivityNotified = false;

    /**
     * Überprüft Beschleunigung auf Anzeichen eines Sturzes.
     */
    private void detectFall(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double acceleration = Math.sqrt(x * x + y * y + z * z);

        if (acceleration > 25) { // Schwelle für Sturz
            triggerFallDetected();
        }
    }

    /**
     * Überwacht Inaktivität über einen längeren Zeitraum.
     */
    private void detectInactivity(SensorEvent event) {
        float movement = Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]);

        long currentTime = System.currentTimeMillis();

        if (movement > 0.5f) {
            lastMovementTime = currentTime;
            inactivityNotified = false;
        }
    }

    /** Zeigt eine Erinnerung zur Bewegung bei längerer Inaktivität */
    private void showInactivityReminderNotification() {
        String[] messages = {
                "Lust auf einen Spaziergang?",
                "Wie wär's mit ein paar Turnübungen?",
                "Zeit, sich ein bisschen zu bewegen!",
                "Bewegung tut gut – los geht's!"
        };
        String message = messages[new java.util.Random().nextInt(messages.length)];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle("Bewegungserinnerung")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /** Runnable zur regelmäßigen Überprüfung von Inaktivität */
    private Runnable inactivityCheckRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if (!inactivityNotified && currentTime - lastMovementTime > INACTIVITY_THRESHOLD) {
                inactivityNotified = true;
                showInactivityReminderNotification();
            }
            inactivityHandler.postDelayed(this, 15 * 60 * 1000); //alle 15 Minuten wird überprüft
        }
    };

    /** Startet die Aktivität zur Sturzerkennung */
    private void triggerFallDetected() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(this, Sturzerkennung.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    /** Erstellt den Notification-Channel für Android O und höher */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sturzerkennung Hintergrunddienst",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    /**
     * Erstellt die Benachrichtigung für den laufenden Service.
     * @return Notification-Objekt für den Vordergrunddienst
     */
    private Notification buildForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TagesBlüte Aktiv")
                .setContentText("Der Sturzerkennungsdienst läuft im Hintergrund")
                .setSmallIcon(R.drawable.warning)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
