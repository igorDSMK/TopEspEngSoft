package com.example.trabalho1.Local;

import com.example.trabalho1.Database.ILocalDataSource;
import com.example.trabalho1.Model.Local;

import java.util.List;

import io.reactivex.Flowable;

public class LocalDataSource implements ILocalDataSource {

    private LocalDAO localDAO;
    private static LocalDataSource mInstance;

    public LocalDataSource (LocalDAO localDAO){
        this.localDAO = localDAO;
    }

    public static LocalDataSource getInstance(LocalDAO localDAO){
        if (mInstance == null) {
            mInstance = new LocalDataSource(localDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<Local> getLocalbyId(int localId) {
        return localDAO.getLocalbyId(localId);
    }

    @Override
    public Flowable<List<Local>> getAllLocais() {
        return localDAO.getAllLocais();
    }

    @Override
    public void insertLocal(Local... locais) {
        localDAO.insertLocal(locais);
    }

    @Override
    public void updateLocal(Local... locais) {
        localDAO.updateLocal(locais);
    }

    @Override
    public void deleteLocal(Local local) {
        localDAO.deleteLocal(local);
    }

    @Override
    public void deleteAllLocais() {
        localDAO.deleteAllLocais();
    }
}
