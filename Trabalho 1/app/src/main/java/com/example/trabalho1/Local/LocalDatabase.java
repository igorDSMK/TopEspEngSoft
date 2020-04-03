package com.example.trabalho1.Local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trabalho1.Model.Local;

import static com.example.trabalho1.Local.LocalDatabase.DATABASE_VERSION;

@Database(entities = Local.class, version = DATABASE_VERSION)
public abstract class LocalDatabase extends RoomDatabase {
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME = "TRABALHO-Database-Room";

    public abstract LocalDAO localDAO();

    private static LocalDatabase mInstance;

    public static LocalDatabase getInstance(Context context){
        if(mInstance == null){
            mInstance = Room.databaseBuilder(context, LocalDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        }
        return mInstance;
    }

}
