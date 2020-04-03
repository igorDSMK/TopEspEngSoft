package com.example.trabalho1;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.example.trabalho1.Database.LocalRepository;
import com.example.trabalho1.Model.Local;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Background extends AsyncTask<String, Integer, String> {

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    FrameLayout progressBarHolder;

    Context context;
    Local localToOperate;

    List<Local> localList = new ArrayList<>();
    ArrayAdapter adapter;

    private LocalRepository localRepository;
    private CompositeDisposable compositeDisposable;

    boolean isToAdd;

    public Background(Context context, Local localToOperate, ArrayAdapter adapter,
                      CompositeDisposable compositeDisposable, List<Local> localList, LocalRepository localRepository,
                      boolean isToAdd, FrameLayout progressBarHolder){
        this.context = context;
        this.localToOperate = localToOperate;
        this.localList = localList;
        this.adapter = adapter;
        this.compositeDisposable = compositeDisposable;
        this.localRepository = localRepository;
        this.isToAdd = isToAdd;
        this.progressBarHolder = progressBarHolder;
    }

        public String getTemp(String url){
        return this.localToOperate.getTemp();
    }

    @Override
    protected String doInBackground(String... urls) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urls[0], null, future, future);

        MySingleton.getInstance(context).addToRequestQueue(request);

        try {
            JSONObject response = future.get();
            JSONArray jsonArray = response.getJSONArray("data");
            localToOperate.setTemp(jsonArray.getJSONObject(0).getString("temp"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(isToAdd) {
            AddLocal();
        } else {
            updateLocal(localToOperate);
        }
        return localToOperate.getTemp();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String temp) {
        super.onPostExecute(temp);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }

    private void AddLocal(){
        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                localRepository.insertLocal(localToOperate);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Toast.makeText(context, "Local Add!", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();
                    }
                });
    }

    private void loadData() {
        Disposable disposable = localRepository.getAllLocais()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Local>>() {
                    @Override
                    public void accept(List<Local> locals) throws Exception {
                        onGetAllLocalsSuccess(locals);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void onGetAllLocalsSuccess(List<Local> locals) {
        localList.clear();
        localList.addAll(locals);
        adapter.notifyDataSetChanged();
    }

    private void updateLocal(final Local local){
        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                localRepository.updateLocal(local);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, "Updated" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        loadData();
                    }
                });
        compositeDisposable.add(disposable);
    }
}
