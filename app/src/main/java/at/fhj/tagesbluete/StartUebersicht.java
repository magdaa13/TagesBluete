package at.fhj.tagesbluete;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartUebersicht extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_uebersicht);

        Button buttonTagesplan = findViewById(R.id.buttonTagesplan);
        buttonTagesplan.setOnClickListener(v -> {
            startActivity(new Intent(StartUebersicht.this, Tagesplan.class));
        });

        Button buttonMeinGarten = findViewById(R.id.buttonMeinGarten);
        buttonMeinGarten.setOnClickListener(v -> {
            startActivity(new Intent(StartUebersicht.this, Garten.class));
        });

        Button buttonNotfallkontakt = findViewById(R.id.buttonNotfallkontakte);
        buttonNotfallkontakt.setOnClickListener(v -> {
            startActivity(new Intent(StartUebersicht.this, Notfallkontakt.class));
        });

        Button buttonlogout = findViewById(R.id.buttonLogout);
        buttonlogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(StartUebersicht.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        TextView dateText = findViewById(R.id.dateText);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d.M.yyyy", Locale.GERMAN);;
        String currentDate = sdf.format(new Date());
        dateText.setText(currentDate);
    }
}
