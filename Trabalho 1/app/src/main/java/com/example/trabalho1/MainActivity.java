package com.example.trabalho1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.trabalho1.Database.LocalRepository;
import com.example.trabalho1.Local.LocalDataSource;
import com.example.trabalho1.Local.LocalDatabase;
import com.example.trabalho1.Model.Local;
import com.example.trabalho1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ListView listLocais;
    private FloatingActionButton fab;
    private FloatingActionButton ref;
    private String city = "";
    private String country = "";
    FrameLayout progressBarHolder;

    //adaptador
    List<Local> localList = new ArrayList<>();
    ArrayAdapter adapter;

    //database
    private CompositeDisposable compositeDisposable;
    private LocalRepository localRepository;

    //Key to API
    private String key = "9ee21faca6034f5e8fa8b1ba06400812";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compositeDisposable = new CompositeDisposable();

        //view
        listLocais = (ListView)findViewById(R.id.lstLocais);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        ref = (FloatingActionButton)findViewById(R.id.ref);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, localList);
        registerForContextMenu(listLocais);
        listLocais.setAdapter(adapter);

        //database
        LocalDatabase localDatabase = LocalDatabase.getInstance(this);
        localRepository = LocalRepository.getInstance(LocalDataSource.getInstance(localDatabase.localDAO()));

        loadData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Request new local data
                final LinearLayout layout = new LinearLayout(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("local:");

                final EditText input_c = new EditText(MainActivity.this);
                input_c.setHint("City");
                layout.addView(input_c);
                final EditText input_C = new EditText(MainActivity.this);
                input_C.setHint("County (2 digits code)");
                layout.addView(input_C);

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        city = input_c.getText().toString();
                        country = input_C.getText().toString();
                        String url = "https://api.weatherbit.io/v2.0/current?city=" + city + "," + country + "&key=" + key;
                        new Background(MainActivity.this, new Local(city, country, ""), adapter, compositeDisposable,
                                localList, localRepository, true, progressBarHolder).execute(url);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < localList.size(); i++){
                    String url =  "https://api.weatherbit.io/v2.0/current?city=" + localList.get(i).getCidade() + "," + localList.get(i).getPais() + "&key=" + key;
                    new Background(MainActivity.this, localList.get(i), adapter, compositeDisposable,
                            localList, localRepository, false, progressBarHolder).execute(url);
                }
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
                        Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void onGetAllLocalsSuccess(List<Local> locals) {
        localList.clear();
        localList.addAll(locals);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        menu.add(Menu.NONE, 0, Menu.NONE, "REFRESH");
        menu.add(Menu.NONE, 1, Menu.NONE, "DELETE");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final Local local = localList.get(info.position);

        switch(item.getItemId()){

            case 0:{ //refresh
                String url =  "https://api.weatherbit.io/v2.0/current?city=" + local.getCidade() + "," + local.getPais() + "&key=" + key;
                new Background(MainActivity.this, local, adapter, compositeDisposable,
                        localList, localRepository, false, progressBarHolder).execute(url);
            }
            break;

            case 1:{ //delete
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to delete this Place Weather?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteLocal(local);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
        }
        return true;
    }

    private void deleteLocal(final Local local) {
        Disposable disposable = io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                localRepository.deleteLocal(local);
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
                        Toast.makeText(MainActivity.this, "Deleted" + throwable.getMessage(), Toast.LENGTH_LONG).show();
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
