package at.fhj.tagesbluete;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Garten extends AppCompatActivity {

    public FrameLayout gartenHintergrund;
    public int pflanzenAbstand = 150;
    public int pflanzengroesse = 200;
    public int abstandVomRand = 50;
    public int pflanzenProErledigte = 3;

    public PflanzeDAO pflanzeDAO;
    public List<Pflanzen> blumenListe = new ArrayList<>();

    public int [] blumenBilder = {
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_garten);

        gartenHintergrund = findViewById(R.id.gartenHintergrund);

        RoomDatenbank db = Room.databaseBuilder(getApplicationContext(),RoomDatenbank.class, "tagesbluete-db").allowMainThreadQueries().build();
        pflanzeDAO = db.pflanzeDAO();

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String nutzername = prefs.getString("nutzername", "StandardUser");
        int erledigte = prefs.getInt("erledigte_gesamt", 0);
        int pflanzenAnzahl = erledigte / pflanzenProErledigte;

        blumenListe = pflanzeDAO.getAllePflanzen(nutzername);

        while(blumenListe.size() < pflanzenAnzahl){
            Pflanzen neu = generiereNeueBlume();
            pflanzeDAO.insert(neu);
            blumenListe.add(neu);
        }

        for (Pflanzen f : blumenListe) {
            zeigeBlume(f);
        }

    }

    private Pflanzen generiereNeueBlume() {
        int drawableId = blumenBilder[new Random().nextInt(blumenBilder.length)];
        int x = generiereZufälligeX();
        int y = generiereZufälligeY();
        return new Pflanzen(drawableId, x, y);
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

    private void fügePflanzeHinzu() {
        ImageView pflanze = new ImageView(this);

        int zufallsIndex = new Random().nextInt(blumenBilder.length);
        pflanze.setImageResource(blumenBilder[zufallsIndex]);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pflanzengroesse, pflanzengroesse);

        int x = generiereZufälligeX();
        int y = generiereZufälligeY();

        params.leftMargin = x;
        params.topMargin = y;

        pflanze.setLayoutParams(params);
        gartenHintergrund.addView(pflanze);
    }

    public int generiereZufälligeX() {
        int breite = getResources().getDisplayMetrics().widthPixels;
        return abstandVomRand + new Random().nextInt(Math.max(1, breite - pflanzengroesse - abstandVomRand));
    }

    public int generiereZufälligeY() {
        int höhe = getResources().getDisplayMetrics().heightPixels;
        return abstandVomRand + new Random().nextInt(Math.max(1, höhe - pflanzengroesse - abstandVomRand - 200));
    }

    }





