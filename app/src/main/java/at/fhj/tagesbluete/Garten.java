package at.fhj.tagesbluete;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Garten extends AppCompatActivity {

    private FrameLayout gartenHintergrund;
    private final int pflanzengroesse = 200;
    private final int abstandVomRand = 50;
    private final Random random = new Random();

    private List<Pflanzen> blumenListe = new ArrayList<>();

    private int[] blumenBilder = {
            R.drawable.flowerblue,
            R.drawable.flowerblue2,
            R.drawable.flowergray,
            R.drawable.flowerorange,
            R.drawable.flowerorange2,
            R.drawable.flowerpink,
            R.drawable.flowerpink2,
            R.drawable.flowerviolet,
            R.drawable.flowerwhite,
            R.drawable.sunflower
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garten);

        gartenHintergrund = findViewById(R.id.gartenHintergrund);

        RoomDatenbank db = Room.databaseBuilder(getApplicationContext(), RoomDatenbank.class, "tagesbluete-db").allowMainThreadQueries().build();
        PflanzeDAO pflanzeDAO = db.pflanzeDAO();

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String nutzername = prefs.getString("nutzername", "StandardUser");
        int erledigte = prefs.getInt("erledigte_gesamt", 0);
        int pflanzenProErledigte = 2;
        int pflanzenAnzahl = erledigte / pflanzenProErledigte;

        blumenListe = pflanzeDAO.getAllePflanzen(nutzername);

        while (blumenListe.size() < pflanzenAnzahl) {
            Pflanzen neu = generiereNeueBlume(nutzername);
            pflanzeDAO.insert(neu);
            blumenListe.add(neu);
        }

        for (Pflanzen f : blumenListe) {
            zeigeBlume(f);
        }

        zeigeStartPopUp();
    }

    private Pflanzen generiereNeueBlume(String nutzername) {
        int drawableId = blumenBilder[random.nextInt(blumenBilder.length)];

        int x, y;
        boolean positionOk;
        do {
            positionOk = true;
            x = generiereZufallX();
            y = generiereZufallY();

            for (Pflanzen p : blumenListe) {
                int dx = p.x - x;
                int dy = p.y - y;
                double abstand = Math.sqrt(dx * dx + dy * dy);
                if (abstand < pflanzengroesse + abstandVomRand) {
                    positionOk = false;
                    break;
                }
            }
        } while (!positionOk);

        Pflanzen neue = new Pflanzen(drawableId, x, y);
        neue.nutzername = nutzername;
        return neue;
    }

    private void zeigeBlume(Pflanzen f) {
        ImageView pflanze = new ImageView(this);
        pflanze.setImageResource(f.drawableId);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pflanzengroesse, pflanzengroesse);
        params.leftMargin = f.x;
        params.topMargin = f.y;
        pflanze.setLayoutParams(params);

        gartenHintergrund.addView(pflanze);
    }

    private int generiereZufallX() {
        int breite = getResources().getDisplayMetrics().widthPixels;
        return abstandVomRand + new Random().nextInt(Math.max(1, breite - pflanzengroesse - abstandVomRand));
    }

    private int generiereZufallY() {
        int hoehe = getResources().getDisplayMetrics().heightPixels * 3; // für Scrollview mehr Platz nach unten
        return abstandVomRand + new Random().nextInt(Math.max(1, hoehe - pflanzengroesse - abstandVomRand));
    }

    private void zeigeStartPopUp() {
        String[] nachrichten = {
                "Erledige deine Aufgaben – und sieh zu, wie dein Garten erblüht!",
                "Ein schöner Garten wächst mit deinen Erfolgen!",
                "Mach mit: Je mehr du schaffst, desto bunter wird dein Garten!"
        };

        int zufallsIndex = new Random().nextInt(nachrichten.length);
        String zufallsNachricht = nachrichten[zufallsIndex];
        String kompletterText = zufallsNachricht + "\n\nTipp: Wische nach unten, um den ganzen Garten zu sehen!";


        new androidx.appcompat.app.AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Willkommen im Garten 🌸")
                .setMessage(kompletterText)
                .setPositiveButton("Los geht's!", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

}
