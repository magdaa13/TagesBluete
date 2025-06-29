package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Repräsentiert eine Aufgabe in der Datenbank.
 * Jede Aufgabe hat einen eindeutigen Bezeichner, einen Titel, eine Beschreibung,
 * ein Datum, eine Uhrzeit, sowie Informationen zur Wiederholung und Erledigung.
 * Zusätzlich wird die Aufgabe einem bestimmten Nutzer zugeordnet.
 */
@Entity
public class Aufgabe {
    /**
     * Eindeutige ID der Aufgabe (wird automatisch generiert).
     */
    @PrimaryKey(autoGenerate = true)
    public int id;
    /**
     * Titel der Aufgabe.
     */
    public String titel;
    /**
     * Ausführliche Beschreibung der Aufgabe.
     */
    public String beschreibung;
    /**
     * Datum der Aufgabe im Format "yyyy-MM-dd" (z.B. 2025-06-28).
     */
    public String datum;
    /**
     * Uhrzeit der Aufgabe im Format "HH:mm" (z.B. 14:30).
     */
    public String uhrzeit;
    /**
     * Gibt an, ob und wie sich die Aufgabe wiederholt (z.B. täglich, wöchentlich).
     */
    public String wiederholung;

    /**
     * Benutzername des Nutzers, dem die Aufgabe zugeordnet ist.
     */
    //Nutzerzuordnung
    public String nutzername;

    /**
     * Status der Aufgabe, ob sie erledigt wurde (true) oder noch offen ist (false).
     */
    public boolean erledigt; //Eine Aufgabe wurde erledigt


}
