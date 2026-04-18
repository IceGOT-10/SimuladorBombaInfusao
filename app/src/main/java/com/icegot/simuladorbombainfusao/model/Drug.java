package com.icegot.simuladorbombainfusao.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drugs")
public class Drug {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    /** ml/h */
    public float defaultRate;

    /** ml/h */
    public float maxSafeRate;

    public Drug(String name, float defaultRate, float maxSafeRate) {
        this.name = name;
        this.defaultRate = defaultRate;
        this.maxSafeRate = maxSafeRate;
    }
}