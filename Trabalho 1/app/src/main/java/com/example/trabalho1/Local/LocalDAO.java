package com.example.trabalho1.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trabalho1.Model.Local;
import java.util.List;
import io.reactivex.Flowable;

@Dao
public interface LocalDAO {

    @Query("SELECT * FROM locais WHERE id=:localId")
    Flowable<Local> getLocalbyId(int localId);

    @Query("SELECT * FROM locais")
    Flowable<List<Local>> getAllLocais();

    @Insert
    void insertLocal(Local... locais);

    @Update
    void updateLocal(Local... locais);

    @Delete
    void deleteLocal(Local local);

    @Query("DELETE FROM locais")
    void deleteAllLocais();
}
