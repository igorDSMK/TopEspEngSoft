package com.example.trabalho1.Database;

import com.example.trabalho1.Model.Local;

import java.util.List;

import io.reactivex.Flowable;

public class LocalRepository implements ILocalDataSource {

    private ILocalDataSource mLocalDataSource;
    private static  LocalRepository mInstance;

    public LocalRepository(ILocalDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static LocalRepository getInstance(ILocalDataSource mLocalDataSource){
        if(mInstance == null){
            mInstance = new LocalRepository(mLocalDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<Local> getLocalbyId(int localId) {
        return mLocalDataSource.getLocalbyId(localId);
    }

    @Override
    public Flowable<List<Local>> getAllLocais() {
        return mLocalDataSource.getAllLocais();
    }

    @Override
    public void insertLocal(Local... locais) {
        mLocalDataSource.insertLocal(locais);
    }

    @Override
    public void updateLocal(Local... locais) {
        mLocalDataSource.updateLocal(locais);
    }

    @Override
    public void deleteLocal(Local local) {
        mLocalDataSource.deleteLocal(local);
    }

    @Override
    public void deleteAllLocais() {
        mLocalDataSource.deleteAllLocais();
    }
}
