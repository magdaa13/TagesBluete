package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pflanzen {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String art;
    public int level;
    public String nutzername;
}
