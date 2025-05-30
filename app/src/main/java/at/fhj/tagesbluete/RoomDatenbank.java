package at.fhj.tagesbluete;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Benutzer.class, Aufgabe.class}, version = 3)
public abstract class RoomDatenbank extends RoomDatabase {

    private static RoomDatenbank instance;
    public abstract BenutzerDAO userDao();
    public abstract AufgabeDAO aufgabeDao();

    public static synchronized RoomDatenbank getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RoomDatenbank.class,
                            "user_database"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }


}
