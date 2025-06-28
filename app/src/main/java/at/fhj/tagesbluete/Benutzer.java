package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Repräsentiert einen Benutzer in der Room-Datenbank.
 *
 * Jeder Benutzer hat eine eindeutige ID, einen Benutzernamen und ein Passwort.
 */
@Entity
public class Benutzer {

    /**
     * Primärschlüssel, wird automatisch generiert.
     */
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String benutzername;
    public String passwort;


}
