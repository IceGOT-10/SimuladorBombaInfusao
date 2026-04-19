package com.icegot.simuladorbombainfusao.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.icegot.simuladorbombainfusao.model.Drug;
import java.util.List;

@Dao
public interface DrugDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Drug drug);

    @Query("SELECT * FROM drugs ORDER BY name ASC")
    LiveData<List<Drug>> getAllDrugs();

    @Query("DELETE FROM drugs")
    void deleteAll();
}