package at.fhj.tagesbluete;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.core.app.NotificationCompat;

public class AufgabenBenachrichtigung extends Worker {

    public AufgabenBenachrichtigung(@NonNull Context context, @NonNull WorkerParameters params){
        super(context,params);
    }
    @NonNull
    @Override
    public Result doWork() {
        String titel = getInputData().getString("titel");

        Context context = getApplicationContext();
        Intent i = new Intent(context, Tagesplan.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        String channelId = "Aufgaben_Channel";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Aufgaben Erinnerungen",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        String[] titelAlternativen = {
                "Hallo! Wie geht es dir?\uD83C\uDF40",
                "Hallo! Zeit für eine Aufgabe! \uD83D\uDD56",
                "Kleine Taten schlagen große Vorsätze. \uD83D\uDCAA",
                "Bereit für die nächste Aufgabe?\uD83C\uDF1F",
                "Heute ist dein Tag!\uD83C\uDF1E",
                "Bereit, etwas zu erreichen?\uD83C\uDF31"
        };

        int zufallsIndex = (int) (Math.random() * titelAlternativen.length);
        String zufallsTitel = titelAlternativen[zufallsIndex];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(zufallsTitel)
                .setContentText("Folgende Aufgabe ist nun fällig: " + titel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());

        return Result.success();
    }
}