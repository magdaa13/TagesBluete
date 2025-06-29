package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) für die Tabelle "Pflanzen".
 * <p>
 * Diese Schnittstelle definiert die Datenbankoperationen für die Pflanzen-Entitäten,
 * einschließlich Einfügen, Aktualisieren und Abfragen von Pflanzen, die einem bestimmten Nutzer gehören.</p>
 */
@Dao
public interface PflanzeDAO {

    /**
     * Fügt eine neue Pflanze in die Datenbank ein.
     *
     * @param pflanzen ist die Pflanzen-Entität, die gespeichert werden soll.
     */
    @Insert
    void insert(Pflanzen pflanzen);

    /**
     * Aktualisiert eine vorhandene Pflanze in der Datenbank.
     *
     * @param pflanzen ist die Pflanzen-Entität mit den aktualisierten Daten.
     */
    @Update
    void update(Pflanzen pflanzen);

    /**
     * Liefert alle Pflanzen, die zu einem bestimmten Nutzer gehören.
     *
     * @param nutzername ist der Nutzername, dessen Pflanzen abgerufen werden sollen.
     * @return Liste aller Pflanzen des Nutzers.
     */
    @Query("SELECT * FROM Pflanzen WHERE nutzername = :nutzername")
    List<Pflanzen> getAllePflanzen(String nutzername);
}
