package at.fhj.tagesbluete;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class Garten extends AppCompatActivity {

    public LinearLayout pflanzenContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_garten);

       pflanzenContainer = findViewById(R.id.pflanzenContainer);

        SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
        String nutzername = prefs.getString("nutzername","");

        PflanzeDAO pflanzeDAO = RoomDatenbank.getInstance(this).pflanzeDAO();
        List<Pflanzen> pflanzen = pflanzeDAO.getAllePflanzen(nutzername);
        zeigePflanzen(pflanzen);
    }

    public void zeigePflanzen(List<Pflanzen>pflanzen){
        pflanzenContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for(Pflanzen p : pflanzen){
            View pflanzenView = inflater.inflate(R.layout.item_pflanze,pflanzenContainer,false);

            ImageView image = pflanzenView.findViewById(R.id.imagePflanze);
            TextView art = pflanzenView.findViewById(R.id.textArt);
            TextView level = pflanzenView.findViewById(R.id.textLevel);

            int resid = getResources().getIdentifier(p.art + "_level" + p.level, "drawable", getPackageName());
            image.setImageResource(resid);

            String artName = Character.toUpperCase(p.art.charAt(0)) + p.art.substring(1);
            art.setText(artName);

            level.setText("Stufe "+ p.level + " von 2");

            pflanzenContainer.addView(pflanzenView);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        aktualisierePflanzenAnzeige();
    }

    public void aktualisierePflanzenAnzeige(){

            SharedPreferences prefs = getSharedPreferences("TagesBluetePrefs", MODE_PRIVATE);
            String nutzername = prefs.getString("nutzername", "");

            PflanzeDAO pflanzeDAO = RoomDatenbank.getInstance(this).pflanzeDAO();
            List<Pflanzen> pflanzen = pflanzeDAO.getAllePflanzen(nutzername);
            zeigePflanzen(pflanzen);

    }

}