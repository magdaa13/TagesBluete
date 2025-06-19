package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AufgabeDAO {
    @Insert
    long insert(Aufgabe aufgabe);

    @Update
    void update(Aufgabe aufgabe);

    @Query("SELECT * FROM Aufgabe WHERE datum = :heutigesDatum AND nutzername = :nutzername ORDER BY uhrzeit ASC")
    List<Aufgabe> getAufgabenFuerDatum(String heutigesDatum, String nutzername);


    @Query("SELECT * FROM Aufgabe WHERE nutzername = :nutzername")
    List<Aufgabe> getAlleAufgaben(String nutzername);

    @Query("DELETE FROM Aufgabe WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM Aufgabe WHERE id = :aufgabeID LIMIT 1")
    Aufgabe findById(int aufgabeID);
}
