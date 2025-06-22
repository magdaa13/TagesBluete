package at.fhj.tagesbluete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AufgabeAdapter extends RecyclerView.Adapter<AufgabeAdapter.AufgabeViewHolder> {
    public List<Aufgabe> aufgabeListe;
    public Set<Integer> selectedPosition = new HashSet<>();
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Aufgabe aufgabe, int position);
    }

    public AufgabeAdapter(List<Aufgabe> aufgabeListe, OnItemClickListener listener){
        this.aufgabeListe = aufgabeListe;
        this.listener = listener;
    }
    public void setAufgabeListe(List<Aufgabe>neueAufgaben){
        this.aufgabeListe = neueAufgaben;
        notifyDataSetChanged();
    }

    @Override
    public AufgabeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aufgabe_item, parent, false);
        return new AufgabeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AufgabeViewHolder holder, int position) {
        Aufgabe aufgabe = aufgabeListe.get(position);

        holder.titelView.setText(aufgabe.titel);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedPosition.contains(position));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked)-> {
            aufgabe.erledigt = isChecked;

            if(isChecked){
                selectedPosition.add(position);
            } else {
                selectedPosition.remove(position);
            }

            RoomDatenbank.getInstance(holder.itemView.getContext())
                    .aufgabeDao()
                    .update(aufgabe);
        });

        holder.itemView.setSelected(selectedPosition.contains(position));
        holder.itemView.setOnClickListener(v -> {
            boolean currentlySelected = selectedPosition.contains(position);
            if(currentlySelected){
                selectedPosition.remove(position);
                holder.checkBox.setChecked(false);
            } else {
                selectedPosition.add(position);
                holder.checkBox.setChecked(true);
            }
            notifyItemChanged(position);

            if(listener != null){
                listener.onItemClick(aufgabe, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aufgabeListe.size();
    }

    //für Mehrfachauswahl
    public List<Aufgabe> getSelectedAufgaben(){
        List<Aufgabe> ausgewählte = new ArrayList<>();
        for(Integer pos : selectedPosition){
            if(pos < aufgabeListe.size()){
                ausgewählte.add(aufgabeListe.get(pos));
            }
        }
        return ausgewählte;
    }

    public void clearSelection(){
        selectedPosition.clear();
        notifyDataSetChanged();
    }

    public static class AufgabeViewHolder extends RecyclerView.ViewHolder {
        TextView titelView;
        CheckBox checkBox;

        public AufgabeViewHolder(@NonNull View itemView) {
            super(itemView);
            titelView = itemView.findViewById(R.id.textViewTitel);
            checkBox = itemView.findViewById(R.id.checkboxErledigt);
        }
}}
