package at.fhj.tagesbluete;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartUebersicht extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_uebersicht);

        TextView dateTextview = findViewById(R.id.dateText);

        String currentDate = new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMAN).format(new Date());

        dateTextview.setText(currentDate);
    }
}