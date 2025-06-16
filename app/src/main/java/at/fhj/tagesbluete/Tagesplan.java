package at.fhj.tagesbluete;

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
            Aufgabe ausgewählteAufgabe = adapter.getSelectedAufgabe();
            if(ausgewählteAufgabe != null){
                db.aufgabeDao().deleteById(ausgewählteAufgabe.id);
                Toast.makeText(this, "Aufgabe gelöscht", Toast.LENGTH_SHORT).show();
                ladeAufgabenFuerHeute();
            } else {
                Toast.makeText(this, "Bitte erst eine Aufgabe auswählen", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAufgabeBearbeiten.setOnClickListener(v -> {
            Aufgabe ausgewählteAufgabe = adapter.getSelectedAufgabe();
            if(ausgewählteAufgabe != null){
                Intent intent = new Intent(this, NeueAufgaben.class);
                intent.putExtra("aufgabe_id", ausgewählteAufgabe.id);  // ID der Aufgabe übergeben
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bitte erst eine Aufgabe auswählen", Toast.LENGTH_SHORT).show();
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