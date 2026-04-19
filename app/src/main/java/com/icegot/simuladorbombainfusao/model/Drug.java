package com.icegot.simuladorbombainfusao.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "drugs")
public class Drug {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private double defaultConcentration;
    private double defaultRate;
    private String unit;

    public Drug(String name, double defaultConcentration, double defaultRate, String unit) {
        this.name = name;
        this.defaultConcentration = defaultConcentration;
        this.defaultRate = defaultRate;
        this.unit = unit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getDefaultConcentration() { return defaultConcentration; }
    public void setDefaultConcentration(double defaultConcentration) { this.defaultConcentration = defaultConcentration; }
    public double getDefaultRate() { return defaultRate; }
    public void setDefaultRate(double defaultRate) { this.defaultRate = defaultRate; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}