package at.fhj.tagesbluete;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Benutzer.class, Aufgabe.class, Pflanzen.class}, version =5, exportSchema = false)
public abstract class RoomDatenbank extends RoomDatabase {

    private static RoomDatenbank instance;
    public abstract BenutzerDAO userDao();
    public abstract AufgabeDAO aufgabeDao();
    public abstract PflanzeDAO pflanzeDAO();

    public static synchronized RoomDatenbank getInstance(Context context) {
        if (instance == null) {
            Builder<RoomDatenbank> userDatabase = Room.databaseBuilder(
                    context.getApplicationContext(),
                    RoomDatenbank.class,
                    "user_database"
            );
            userDatabase.fallbackToDestructiveMigration();
            userDatabase.allowMainThreadQueries();
            instance = userDatabase
                    .build();
        }
        return instance;
    }


}
