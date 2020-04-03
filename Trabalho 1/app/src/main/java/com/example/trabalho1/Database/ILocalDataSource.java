package com.example.trabalho1.Database;

import com.example.trabalho1.Model.Local;

import java.util.List;

import io.reactivex.Flowable;

public interface ILocalDataSource {

    Flowable<Local> getLocalbyId(int localId);
    Flowable<List<Local>> getAllLocais();
    void insertLocal(Local... locais);
    void updateLocal(Local... locais);
    void deleteLocal(Local local);
    void deleteAllLocais();
}
