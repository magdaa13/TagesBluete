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

import java.util.List;
import java.util.Random;


public class Garten extends AppCompatActivity {

    public FrameLayout gartenHintergrund;
    public int pflanzenAbstand = 150;
    public int pflanzengroesse = 200;
    public int abstandVomRand = 50;
    public int pflanzenProErledigte = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_garten);

        gartenHintergrund = findViewById(R.id.gartenHintergrund);

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        int erledigte = prefs.getInt("erledigte_gesamt", 0);


        int pflanzenAnzahl = erledigte / 10;

        for (int i = 0; i < pflanzenAnzahl; i++) {
            fügePflanzeHinzu();
        }
    }


    private void fügePflanzeHinzu() {
        ImageView pflanze = new ImageView(this);
        pflanze.setImageResource(R.drawable.crocus1);

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





