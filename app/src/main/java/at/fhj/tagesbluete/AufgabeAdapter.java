package at.fhj.tagesbluete;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AufgabeAdapter extends RecyclerView.Adapter<AufgabeAdapter.AufgabeViewHolder> {
    public List<Aufgabe> aufgabeListe;
    public int selectedPosition = RecyclerView.NO_POSITION;
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
        holder.checkBox.setChecked(aufgabe.erledigt);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked)-> {
            aufgabe.erledigt = isChecked;

            if(isChecked){
                selectedPosition = holder.getAdapterPosition();
            } else if(selectedPosition == holder.getAdapterPosition()){
                selectedPosition = RecyclerView.NO_POSITION;
            }

            RoomDatenbank.getInstance(holder.itemView.getContext())
                    .aufgabeDao()
                    .update(aufgabe);
        });

        holder.itemView.setSelected(selectedPosition == position);
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);

            if(listener != null){
                listener.onItemClick(aufgabe, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aufgabeListe.size();
    }

    public Aufgabe getSelectedAufgabe(){
        if(selectedPosition != RecyclerView.NO_POSITION){
            return aufgabeListe.get(selectedPosition);
        }
        return null;
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
