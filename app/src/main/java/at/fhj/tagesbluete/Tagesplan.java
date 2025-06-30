package at.fhj.tagesbluete;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import java.util.List;
import java.util.Random;
/**
 * Zur Verwaltung des Tagesplans.
 * Nutzer können Aufgaben hinzufügen, bearbeiten, erledigen und löschen.
 * Erledigte Aufgaben werden gezählt und belohnen am 10 erledigten Aufgaben mit einer Pflanze.
 */
public class Tagesplan extends AppCompatActivity {

    /**
     * Adapter für die Anzeige der Aufgaben in der RecyclerView.
     */
    private AufgabeAdapter adapter;

    /**
     * Zugriffspunkt auf die lokale Room-Datenbank.
     */
    private RoomDatenbank db;

    /**
     * RecyclerView zur Darstellung der Aufgabenliste.
     */
    private RecyclerView recyclerView;

    /**
     * Initialisiert die Activity, setzt die Layout-Elemente, lädt Aufgaben und
     * definiert die Button-Klick-Listener für Aufgabenverwaltung.
     * @param savedInstanceState vorheriger Status der Activity, falls vorhanden
     */
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
            List<Aufgabe> ausgewählteAufgaben = adapter.getSelectedAufgaben(); //welche Aufgaben aktuell vom Benutzer ausgewählt

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
                alarmManager.cancel(pendingIntent); //Falls Aufgabe gelöscht wird, wird geplanter Alarm abgebrochen
            }

            db.aufgabeDao().deleteById(aufgabe.id);
}
            Toast.makeText(this, "Aufgabe gelöscht!", Toast.LENGTH_SHORT).show();

            adapter.clearSelection(); //Auswahl zurücksetzen nach Löschen
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

            List<Aufgabe> neuErledigte = adapter.markiereAusgewählteAlsErledigt();
            for(int i = 0; i<neuErledigte.size();i++){
                speichereFortschrittUndBelohne();
            }

            Toast.makeText(this,"Aufgabe(n) erledigt!",Toast.LENGTH_SHORT).show();

            adapter.clearSelection(); //Auswahl zurücksetzen nach Erledigen
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
    /**
     * Lädt alle Aufgaben für den heutigen Tag aus der Datenbank,
     * filtert sie anhand der Wiederholungsregel und zeigt sie in der RecyclerView an.
     */
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

    /**
     * Filtert Aufgaben anhand ihrer Wiederholungsregel (täglich, wöchentlich, kein).
     * @param alleAufgaben Liste aller Aufgaben
     * @return Liste der Aufgaben, die heute angezeigt werden sollen
     */
    private List<Aufgabe>filterAufgabenFürHeute(List<Aufgabe> alleAufgaben){
        List<Aufgabe>result = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date heute = new Date(); //aktueller Zeitpunkt
        Calendar heuteCal = Calendar.getInstance();
        heuteCal.setTime(heute);
        int heuteWochentag = heuteCal.get(Calendar.DAY_OF_WEEK);

        for (Aufgabe a : alleAufgaben) {
            try {
                Date aufgabenDatum = sdf.parse(a.datum);

                if ("täglich".equals(a.wiederholung)) {
                    result.add(a);// Immer hinzufügen
                } else if ("wöchentlich".equals(a.wiederholung)) {
                    if (aufgabenDatum != null) {
                        Calendar aufgabenCal = Calendar.getInstance();
                        aufgabenCal.setTime(aufgabenDatum);
                        int aufgabenWochentag = aufgabenCal.get(Calendar.DAY_OF_WEEK);

                        if (aufgabenWochentag == heuteWochentag) {
                            result.add(a);//gleicher Wochentag - anzeigen
                        }
                    }
                } else {
                    // Keine Wiederholung, exaktes Datum
                    if (aufgabenDatum != null && sdf.format(aufgabenDatum).equals(sdf.format(heute))) {
                        result.add(a); //nur anzeigen, wenn Datum heute ist
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Prüft, ob basierend auf der Gesamtanzahl erledigter Aufgaben eine neue Pflanze
     * freigeschaltet wird. Wenn ja, wird eine zufällige Pflanze eingefügt.
     * @param erledigteGesamt Anzahl aller erledigten Aufgaben insgesamt
     * @return true, wenn eine neue Pflanze freigeschaltet wurde, sonst false
     */
    private boolean belohneMitPflanze(int erledigteGesamt){
        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String nutzername = prefs.getString("nutzername","");
        PflanzeDAO pflanzeDAO = db.pflanzeDAO();

        boolean neuePflanzeFreigeschaltet = false;

        if(erledigteGesamt % 10 == 0){
            int [] bilder = Garten.blumenBilder;
            int zufallsBild = bilder[new Random().nextInt(bilder.length)];

            Pflanzen neue = new Pflanzen();
            neue.art = zufallsBild;
            neue.nutzername = nutzername;

            pflanzeDAO.insert(neue);
            neuePflanzeFreigeschaltet = true;

        }

        return neuePflanzeFreigeschaltet;

    }
    /**
     * Speichert den Fortschritt der erledigten Aufgaben und ruft
     * die Belohnungsfunktion auf. Bei Freischaltung wird ein Dialog angezeigt.
     */
    private void speichereFortschrittUndBelohne(){
        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int erledigteZähler = prefs.getInt("erledigte_gesamt",0);
        erledigteZähler++;

        editor.putInt("erledigte_gesamt", erledigteZähler);
        editor.apply();

        boolean neuePflanze = belohneMitPflanze(erledigteZähler);

        if(neuePflanze){
            zeigePflanzenFreischaltDialog();
        }
    }
    /**
     * Zeigt einen Dialog an, der den Nutzer über eine neue freigeschaltete Pflanze informiert.
     */
    private void zeigePflanzenFreischaltDialog() {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("Glückwunsch 🌱")
                .setMessage("Du hast eine neue Pflanze freigeschaltet!\nSchau gleich in deinem Garten vorbei – er wird von Tag zu Tag schöner.")
                .setPositiveButton("Super!", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
    }
