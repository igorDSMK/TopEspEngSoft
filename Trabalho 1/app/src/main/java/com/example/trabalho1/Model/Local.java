package com.example.trabalho1.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "locais")
public class Local {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "cidade")
    private String cidade;
    @ColumnInfo(name = "pais")
    private String pais;
    @ColumnInfo(name = "temp")
    private String temp;

    public Local(){}

    @Ignore
    public Local(String cidade, String pais, String temp){
        this.cidade = cidade;
        this.pais = pais;
        this.temp = temp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return new StringBuilder(cidade).append(" - ").append(pais).append(": ").append(temp).append("°C").toString();
    }
}
