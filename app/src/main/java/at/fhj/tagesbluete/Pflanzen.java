package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Pflanzen {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String art;
    public int level;
    public String nutzername;

    public int drawableId;
    public int x;
    public int y;

    public Pflanzen() {
    }

    // Dein eigener Konstruktor
    @Ignore
    public Pflanzen(int drawableId, int x, int y) {
        this.drawableId = drawableId;
        this.x = x;
        this.y = y;
    }

}
