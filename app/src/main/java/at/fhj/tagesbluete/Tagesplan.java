package at.fhj.tagesbluete;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class Tagesplan extends AppCompatActivity {

    public List<Aufgabe> aufgabenListe;
    public AufgabeAdapter adapter;
    public RoomDatenbank db;
    public RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tagesplan);

        recyclerView = findViewById(R.id.recyclerViewAufgaben);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button buttonAufgabeLöschen = findViewById(R.id.buttonAufgabeLöschen);
        Button buttonAufgabeBearbeiten = findViewById(R.id.buttonAufgabeBearbeiten);
        FloatingActionButton fab = findViewById(R.id.fabAddAufgabe);

        buttonAufgabeLöschen.setOnClickListener(v -> {
            List<Aufgabe> ausgewählteAufgaben = adapter.getSelectedAufgaben();

            if (ausgewählteAufgaben.isEmpty()) {
                Toast.makeText(this, "Bitte erst eine Aufgabe auswählen", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Aufgabe aufgabe : ausgewählteAufgaben) {
                Intent intent = new Intent(this, AufgabenBenachrichtigung.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        aufgabe.id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent); //
            }

            db.aufgabeDao().deleteById(aufgabe.id);
}
            Toast.makeText(this, "Aufgabe gelöscht!", Toast.LENGTH_SHORT).show();

            adapter.clearSelection();
            ladeAufgabenFuerHeute();
        });

        buttonAufgabeBearbeiten.setOnClickListener(v -> {
            List<Aufgabe> ausgewählteAufgaben = adapter.getSelectedAufgaben();

            if (ausgewählteAufgaben.isEmpty()) {
                Toast.makeText(this, "Bitte erst eine Aufgabe auswählen", Toast.LENGTH_SHORT).show();
            } else if (ausgewählteAufgaben.size() > 1) {
                Toast.makeText(this, "Sie können jeweils nur eine Aufgabe bearbeiten, bitte nur eine auswählen!", Toast.LENGTH_SHORT).show();
            } else {
                Aufgabe ausgewählteAufgabe = ausgewählteAufgaben.get(0);
                if (ausgewählteAufgabe.erledigt) {
                    Intent intent = new Intent(this, NeueAufgaben.class);
                    intent.putExtra("aufgabe_id", ausgewählteAufgabe.id);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Aufgabe ist noch nicht erledigt", Toast.LENGTH_SHORT).show();
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Tagesplan.this, NeueAufgaben.class));
            }
        });
        db = RoomDatenbank.getInstance(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ladeAufgabenFuerHeute();
    }
    private void ladeAufgabenFuerHeute(){
        String heute = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(new Date());

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String nutzername = prefs.getString("nutzername", "");

        aufgabenListe = db.aufgabeDao().getAufgabenFuerDatum(heute, nutzername);

        if(adapter == null){
            adapter = new AufgabeAdapter(aufgabenListe, (aufgabe, position) -> {

            });
            recyclerView.setAdapter(adapter);
        }else{
            adapter.setAufgabeListe(aufgabenListe);
        }
    }
}