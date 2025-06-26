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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Random;

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
        db = RoomDatenbank.getInstance(this);

        recyclerView = findViewById(R.id.recycler_aufgaben);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button buttonAufgabeLöschen = findViewById(R.id.button_aufgabelöschen);
        Button buttonAufgabeBearbeiten = findViewById(R.id.button_aufgabebearbeiten);
        Button buttonAufgabeIstErledigt = findViewById(R.id.button_aufgabeerledigen);
        Button buttonAufgabeHinzufügen = findViewById(R.id.button_neueaufgabe);

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
                if (!ausgewählteAufgabe.erledigt) {
                    Intent intent = new Intent(this, NeueAufgaben.class);
                    intent.putExtra("aufgabe_id", ausgewählteAufgabe.id);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Erledigte Aufgaben können nicht mehr bearbeitet werden!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAufgabeIstErledigt.setOnClickListener(v ->{
            List<Aufgabe>ausgewählteAufgaben = adapter.getSelectedAufgaben();

            if(ausgewählteAufgaben.isEmpty()){
                Toast.makeText(this,"Bitte eine Aufgabe auswählen", Toast.LENGTH_SHORT).show();
                return;
            }

            adapter.markiereAusgewählteAlsErledigt();
            for(int i = 0; i<ausgewählteAufgaben.size();i++){
                speichereFortschrittUndBelohne();
            }

            Toast.makeText(this,"Aufgabe(n) erledigt!",Toast.LENGTH_SHORT).show();

            adapter.clearSelection();
            ladeAufgabenFuerHeute();
        });

        buttonAufgabeHinzufügen.setOnClickListener(v -> {
            Intent intent = new Intent(Tagesplan.this, NeueAufgaben.class);
            startActivity(intent);
        });
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

        List<Aufgabe> alleAufgaben = db.aufgabeDao().getAufgabenFuerDatum(heute, nutzername);
        List<Aufgabe> heutigeAufgaben = filterAufgabenFürHeute(alleAufgaben);

        if(adapter == null){
            adapter = new AufgabeAdapter(this, heutigeAufgaben, (aufgabe, position) -> {

            });
            recyclerView.setAdapter(adapter);
        }else{
            adapter.setAufgabeListe(heutigeAufgaben);
        }
    }

    public List<Aufgabe>filterAufgabenFürHeute(List<Aufgabe> alleAufgaben){
        List<Aufgabe>result = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date heute = new Date();
        Calendar heuteCal = Calendar.getInstance();
        heuteCal.setTime(heute);
        int heuteWochentag = heuteCal.get(Calendar.DAY_OF_WEEK); // 1=Sonntag ... 7=Samstag

        for (Aufgabe a : alleAufgaben) {
            try {
                Date aufgabenDatum = sdf.parse(a.datum);  // a.datum als String in deinem Modell

                if ("täglich".equals(a.wiederholung)) {
                    // Immer hinzufügen
                    result.add(a);
                } else if ("wöchentlich".equals(a.wiederholung)) {
                    if (aufgabenDatum != null) {
                        Calendar aufgabenCal = Calendar.getInstance();
                        aufgabenCal.setTime(aufgabenDatum);
                        int aufgabenWochentag = aufgabenCal.get(Calendar.DAY_OF_WEEK);

                        if (aufgabenWochentag == heuteWochentag) {
                            result.add(a);
                        }
                    }
                } else {
                    // Keine Wiederholung, exaktes Datum
                    if (aufgabenDatum != null && sdf.format(aufgabenDatum).equals(sdf.format(heute))) {
                        result.add(a);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public void belohneMitPflanze(int erledigteGesamt){
        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String nutzername = prefs.getString("nutzername","");
        PflanzeDAO pflanzeDAO = db.pflanzeDAO();

        List<Pflanzen> alle = pflanzeDAO.getAllePflanzen(nutzername);

        if(erledigteGesamt % 5 == 0 && !alle.isEmpty()){
            for(Pflanzen p : alle){
                if(p.level < 2){
                    p.level++;
                    pflanzeDAO.update(p);
                    break;
                }
            }
        }

        if(erledigteGesamt % 10 == 0){
            String [] arten = {"rose", "tulpe", "sonnenblume"};
            String zufallsArt = arten[new Random().nextInt(arten.length)];

            Pflanzen neue = new Pflanzen();
            neue.art=zufallsArt;
            neue.level=0;
            neue.nutzername = nutzername;

            pflanzeDAO.insert(neue);

        }

    }
    public void speichereFortschrittUndBelohne(){
        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int erledigteZähler = prefs.getInt("erledigte_gesamt",0);
        erledigteZähler++;

        editor.putInt("erledigte_gesamt", erledigteZähler);
        editor.apply();
        belohneMitPflanze(erledigteZähler);

    }
    }
