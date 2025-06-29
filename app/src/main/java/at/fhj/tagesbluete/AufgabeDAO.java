package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) für die Entität Aufgabe.
 * Definiert Methoden zum Einfügen, Aktualisieren, Abfragen und Löschen von Aufgaben
 * innerhalb der Room-Datenbank.
 */
@Dao
public interface AufgabeDAO {

    /**
     * Fügt eine neue Aufgabe in die Datenbank ein.
     * @param aufgabe Die Aufgabe, die eingefügt werden soll.
     * @return Die ID der neu eingefügten Aufgabe.
     */
    @Insert
    long insert(Aufgabe aufgabe);

    /**
     * Aktualisiert eine vorhandene Aufgabe in der Datenbank.
     * @param aufgabe Die zu aktualisierende Aufgabe.
     */
    @Update
    void update(Aufgabe aufgabe);

    /**
     * Aktualisiert eine vorhandene Aufgabe in der Datenbank.
     * @param aufgabe Die zu aktualisierende Aufgabe.
     */
    @Update
    void updateAll(Aufgabe aufgabe);

    /**
     * Gibt alle Aufgaben für ein bestimmtes Datum und einen bestimmten Benutzer zurück,
     * sortiert nach Uhrzeit aufsteigend.
     *
     * @param heutigesDatum das Datum im Format "dd.MM.yyyy".
     * @param nutzername ist der Benutzername.
     * @return Liste der Aufgaben für dieses Datum.
     */
    @Query("SELECT * FROM Aufgabe WHERE datum = :heutigesDatum AND nutzername = :nutzername ORDER BY uhrzeit ASC")
    List<Aufgabe> getAufgabenFuerDatum(String heutigesDatum, String nutzername);

    /**
     * Gibt alle Aufgaben eines bestimmten Benutzers zurück.
     *
     * @param nutzername ist der Benutzername.
     * @return Liste aller Aufgaben des Benutzers.
     */
    @Query("SELECT * FROM Aufgabe WHERE nutzername = :nutzername")
    List<Aufgabe> getAlleAufgaben(String nutzername);

    /**
     * Löscht eine Aufgabe anhand ihrer ID.
     *
     * @param id Die ID der zu löschenden Aufgabe.
     */
    @Query("DELETE FROM Aufgabe WHERE id = :id")
    void deleteById(int id);

    /**
     * Findet eine Aufgabe anhand ihrer ID.
     *
     * @param aufgabeID Die ID der Aufgabe.
     * @return Die entsprechende Aufgabe, oder null wenn nicht gefunden.
     */
    @Query("SELECT * FROM Aufgabe WHERE id = :aufgabeID LIMIT 1")
    Aufgabe findById(int aufgabeID);
}
