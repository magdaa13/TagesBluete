package at.fhj.tagesbluete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AufgabeAdapter extends RecyclerView.Adapter<AufgabeAdapter.AufgabeViewHolder> {
    public List<Aufgabe> aufgabeListe;

    public AufgabeAdapter(List<Aufgabe> aufgabeListe){
        this.aufgabeListe = aufgabeListe;
    }
    public void setAufgabeListe(List<Aufgabe>neueAufgaben){
        this.aufgabeListe = neueAufgaben;
    }
    @NonNull
    @Override
    public AufgabeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new AufgabeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AufgabeViewHolder holder, int position) {
        Aufgabe aufgabe = aufgabeListe.get(position);
        holder.titelView.setText(aufgabe.titel);
        holder.beschreibungView.setText(aufgabe.beschreibung);
    }

    @Override
    public int getItemCount() {
        return aufgabeListe.size();
    }

    public static class AufgabeViewHolder extends RecyclerView.ViewHolder {
        TextView titelView;
        TextView beschreibungView;

        public AufgabeViewHolder(@NonNull View itemView) {
            super(itemView);
            titelView = itemView.findViewById(android.R.id.text1);
            beschreibungView = itemView.findViewById(android.R.id.text2);
        }
}}
