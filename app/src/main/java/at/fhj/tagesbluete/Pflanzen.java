package at.fhj.tagesbluete;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
/**
 * Repräsentiert eine Pflanze im virtuellen Garten der App "TagesBlüte".
 * Diese Entity wird in einer Room-Datenbank gespeichert und enthält Informationen
 * über die Pflanzenart, Position im Garten sowie die zugehörige Nutzerkennung.
 */
@Entity
public class Pflanzen {
    /**
     * Eindeutige ID der Pflanze (wird automatisch generiert).
     */
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String art;

    public String nutzername;
    public int drawableId;
    public int x;
    public int y;

    /**
     * Leerer Konstruktor (erforderlich für Room).
     */
    public Pflanzen() {
    }

    /**
     * Konstruktor zum Erstellen einer Pflanze mit Bild und Position.
     * Wird z. B. verwendet, um neue Pflanzen temporär zu erstellen, ohne sie sofort zu speichern.
     *
     * @param drawableId Drawable-ID der Pflanze
     * @param x X-Koordinate im Garten
     * @param y Y-Koordinate im Garten
     */
    @Ignore
    public Pflanzen(int drawableId, int x, int y) {
        this.drawableId = drawableId;
        this.x = x;
        this.y = y;
    }

}
