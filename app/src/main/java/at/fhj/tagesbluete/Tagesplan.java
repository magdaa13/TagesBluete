package at.fhj.tagesbluete;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        FloatingActionButton fab = findViewById(R.id.fabAddAufgabe);
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
        String heute = new SimpleDateFormat("dd.MM.yyyy",Locale.GERMAN).format(new Date());
        aufgabenListe = db.aufgabeDao().getAufgabenFuerDatum(heute);

        if(adapter == null){
            adapter = new AufgabeAdapter(aufgabenListe);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.setAufgabeListe(aufgabenListe);
            adapter.notifyDataSetChanged();
        }
    }
}