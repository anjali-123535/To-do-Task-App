package com.example.to_do;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.to_do.database.AppDatabase;
import com.example.to_do.database.TaskEntry;

import java.util.List;
// we are using this class because everytime we rotates our phone Activity destroys and is recreated hence
// onCreate is called and the loading from the database takes place again even if there is no change or updates to be loadede

public class MainViewModel extends AndroidViewModel {
    LiveData<List<TaskEntry>>liveData;
    private static final String TAG=MainViewModel.class.getName();
    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db=AppDatabase.getInstance(this.getApplication());
        Log.d(TAG,"Retrieving the data from the database in viewmodel");
        liveData=db.taskDao().loadAllTaks();
    }

    public LiveData<List<TaskEntry>> getLiveData() {
        return liveData;
    }
}
