package at.fhj.tagesbluete;

import android.annotation.SuppressLint;
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
import android.os.IBinder;
import android.os.Looper;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.logging.LogRecord;

public class SensorService extends Service implements SensorEventListener {

    public SensorManager sensorManager;
    public Sensor accelerometer;
    public Sensor motionSensor;

    public long lastMovementTime = System.currentTimeMillis();
    public static final long INACTIVITY_THRESHOLD = 1000 * 60 * 30;
    public Handler handler;
    public String CHANNEL_ID = "SturzerkennungsServiceChannel";

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1,buildForegroundNotification());
        handler = new Handler(Looper.getMainLooper()) {
        };

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // oder TYPE_ACCELEROMETER

        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (motionSensor != null)
            sensorManager.registerListener(this, motionSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectFall(event);
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            detectInactivity(event);
        }
    }

    private void detectFall(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double acceleration = Math.sqrt(x*x + y*y + z*z);

        if (acceleration > 25) { // Schwelle
            triggerFallDetected();
        }
    }

    private void detectInactivity(SensorEvent event) {
        float movement = Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]);
        if (movement > 0.5) {
            lastMovementTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMovementTime > INACTIVITY_THRESHOLD) {
                showInactivityReminder();
            }
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    private void triggerFallDetected() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(this, Sturzerkennung.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void showInactivityReminder() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(), "Bewegungserinnerung: Bitte machen Sie einen kurzen Spaziergang.", Toast.LENGTH_LONG).show();
        });
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sturzerkennung Hintergrunddienst",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
    public Notification buildForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sturzerkennung aktiv")
                .setContentText("Die App überwacht Bewegungen im Hintergrund.")
                .setSmallIcon(R.drawable.warning)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
