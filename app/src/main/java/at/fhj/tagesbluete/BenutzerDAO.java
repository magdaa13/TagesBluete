package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Data Access Object (DAO) für die {@link Benutzer}-Entität.
 * Definiert Methoden zum Einfügen und Abfragen von Benutzerdaten in der Room-Datenbank.
 */
@Dao
public interface BenutzerDAO {

    /**
     * Fügt einen neuen Benutzer in die Datenbank ein.
     *
     * @param benutzer Das {@link Benutzer}-Objekt, das gespeichert werden soll.
     */
    @Insert
    void insert(Benutzer benutzer);

    /**
     * Führt einen Login-Vorgang durch, indem ein Benutzer mit passendem Benutzernamen und Passwort gesucht wird.
     *
     * @param benutzername Der Benutzername.
     * @param passwort     Das Passwort (im Klartext, sofern nicht verschlüsselt gespeichert).
     * @return Der passende {@link Benutzer}, oder {@code null}, wenn kein Treffer gefunden wurde.
     */
    @Query("SELECT * FROM Benutzer WHERE benutzername = :benutzername AND passwort = :passwort LIMIT 1")
    Benutzer login(String benutzername, String passwort);

    /**
     * Sucht nach einem Benutzer anhand des Benutzernamens.
     *
     * @param benutzername Der Benutzername.
     * @return Der passende {@link Benutzer}, oder {@code null}, wenn keiner gefunden wurde.
     */
    @Query("SELECT * FROM Benutzer WHERE benutzername = :benutzername LIMIT 1")
    Benutzer findByUsername(String benutzername);
}
