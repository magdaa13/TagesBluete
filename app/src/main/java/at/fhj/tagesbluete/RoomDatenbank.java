package at.fhj.tagesbluete;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
/**
 * Room-Datenbankklasse für die TagesBlüte-App.
 * Beinhaltet Tabellen für Benutzer, Aufgaben und Pflanzen.
 */
@Database(entities = {Benutzer.class, Aufgabe.class, Pflanzen.class}, version =5, exportSchema = false)
public abstract class RoomDatenbank extends RoomDatabase {

    /** Instanz der Datenbank */
    private static RoomDatenbank instance;
    /**
     * @return DAO für Benutzer-Tabelle
     */
    public abstract BenutzerDAO userDao();
    /**
     * @return DAO für Aufgaben-Tabelle
     */
    public abstract AufgabeDAO aufgabeDao();
    /**
     * @return DAO für Pflanzen-Tabelle
     */

    public abstract PflanzeDAO pflanzeDAO();

    /**
     * Gibt eine Singleton-Instanz der Room-Datenbank zurück.
     *
     * @param context Anwendungskontext
     * @return Instanz von RoomDatenbank
     */
    public static synchronized RoomDatenbank getInstance(Context context) {
        if (instance == null) {
            // Hier wird die Datenbank gebaut, wenn instance noch null ist
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RoomDatenbank.class,
                            "user_database"
                    )
                    .fallbackToDestructiveMigration()  // Löscht DB bei Versionswechsel
                    .allowMainThreadQueries()          // Nur zu Testzwecken! Nicht in Produktiv-Apps empfohlen
                    .build();
        }
        return instance;
    }





}
