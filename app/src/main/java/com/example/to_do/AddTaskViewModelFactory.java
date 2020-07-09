package com.example.to_do;


// TODO (1) Make this class extend ViewModel ViewModelProvider.NewInstanceFactory

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.to_do.database.AppDatabase;

public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory{



    // TODO (2) Add two member variables. One for the database and one for the taskId
    private AppDatabase db;
    private int taskId;



    // TODO (3) Initialize the member variables in the constructor with the parameters received

    public AddTaskViewModelFactory(AppDatabase db, int taskId) {
        this.db = db;
        this.taskId = taskId;
    }


    // TODO (4) Uncomment the following method

    // Note: This can be reused with minor modifications

    @Override

    public <T extends ViewModel> T create(Class<T> modelClass) {

        //noinspection unchecked

        return (T) new AddTaskViewModel(db, taskId);

    }

}