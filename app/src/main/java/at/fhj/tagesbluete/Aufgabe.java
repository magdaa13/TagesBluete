package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Aufgabe {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String titel;
    public String beschreibung;
    public String datum;
    public String uhrzeit;
    public String wiederholung;


}
