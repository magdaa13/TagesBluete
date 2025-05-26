package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Benutzer {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String benutzername;
    public String passwort;


    // Konstruktor
    public Benutzer() {
        this.benutzername = benutzername;
        this.passwort = passwort;
    }

}
