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
    public Benutzer(String benutzername, String passwort) {
        this.benutzername = benutzername;
        this.passwort = passwort;
    }

}
