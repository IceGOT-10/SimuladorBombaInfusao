package com.icegot.simuladorbombainfusao.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.icegot.simuladorbombainfusao.dao.DrugDao;
import com.icegot.simuladorbombainfusao.model.Drug;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Drug.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DrugDao drugDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "infusion_pump_db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                DrugDao dao = INSTANCE.drugDao();
                dao.deleteAll();

                dao.insert(new Drug("Soro Fisiológico 0,9%", 0.9, 125.0, "ml/h"));
                dao.insert(new Drug("Insulina Regular", 100.0, 5.0, "UI/ml"));
                dao.insert(new Drug("Noradrenalina", 4.0, 10.0, "mg/ml"));
            });
        }
    };
}