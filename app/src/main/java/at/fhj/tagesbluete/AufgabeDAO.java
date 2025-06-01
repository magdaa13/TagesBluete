package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AufgabeDAO {
    @Insert
    void insert(Aufgabe aufgabe);

    @Update
    void update(Aufgabe aufgabe);

    @Query("SELECT * FROM Aufgabe WHERE datum = :heutigesDatum ORDER BY uhrzeit ASC")
    List<Aufgabe> getAufgabenFuerDatum(String heutigesDatum);

    @Query("SELECT * FROM Aufgabe")
    List<Aufgabe> getAlleAufgaben();

    @Query("DELETE FROM Aufgabe WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM Aufgabe WHERE id = :aufgabeID LIMIT 1")
    Aufgabe findById(int aufgabeID);
}
