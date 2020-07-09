package com.example.to_do;

// TODO (5) Make this class extend ViewModel

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.to_do.database.AppDatabase;
import com.example.to_do.database.TaskEntry;

public class AddTaskViewModel extends ViewModel {



    // TODO (6) Add a task member variable for the TaskEntry object wrapped in a LiveData
LiveData<TaskEntry>tasks;


    // TODO (8) Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable

    public AddTaskViewModel(AppDatabase db,int id) {
        tasks=db.taskDao().loadTaskById(id);
    }

    // Note: The constructor should receive the database and the taskId



    // TODO (7) Create a getter for the task variable

}