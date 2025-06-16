package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface BenutzerDAO {

    @Insert
    void insert(Benutzer benutzer);

    @Query("SELECT * FROM Benutzer WHERE benutzername = :benutzername AND passwort = :passwort LIMIT 1")
    Benutzer login(String benutzername, String passwort);


    @Query("SELECT * FROM Benutzer WHERE benutzername = :benutzername LIMIT 1")
    Benutzer findByUsername(String benutzername);


}
