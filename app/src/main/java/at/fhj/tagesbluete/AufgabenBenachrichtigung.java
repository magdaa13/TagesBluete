package at.fhj.tagesbluete;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AufgabenBenachrichtigung extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        String titel = intent.getStringExtra("titel");

        Intent i = new Intent(context,Tagesplan.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        String channelId = "Aufgaben_Channel";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Aufgaben Erinnerungen",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }
        //Array mit zufälligen Titeln
        String[] titelAlternativen = {
                "Hallo! Wie geht es dir? \uD83C\uDF40",
                "Hallo! Zeit für eine Aufgabe! \uD83D\uDD56",
                "Kleine Taten schlagen große Vorsätze. \uD83D\uDCAA",
                "Bereit für die nächste Aufgabe? \uD83C\uDF1F",
                "Schön, dass du da bist!\uD83C\uDF3B",
                "Bereit, etwas zu erreichen?\uD83C\uDF31",
                "Kümmer dich gut um dich!\uD83C\uDF1F",
                "Heute ist dein Tag!\uD83C\uDF1E"
        };

        //zufällig auswählen
        int zufallsIndex = (int) (Math.random() * titelAlternativen.length);
        String zufallsTitel = titelAlternativen[zufallsIndex];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(zufallsTitel)
                .setContentText("Folgende Aufgabe ist nun fällig: " + titel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(),builder.build());
    }
}
