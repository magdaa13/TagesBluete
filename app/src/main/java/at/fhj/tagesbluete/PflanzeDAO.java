package at.fhj.tagesbluete;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PflanzeDAO {
    @Insert
    void insert(Pflanzen pflanzen);

    @Update
    void update(Pflanzen pflanzen);

    @Query("SELECT * FROM Pflanzen WHERE nutzername = :nutzername")
    List<Pflanzen>getAllePflanzen(String nutzername);

}
