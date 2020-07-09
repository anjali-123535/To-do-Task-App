package com.example.to_do.database;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {TaskEntry.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public static final String LOG_TAG=AppDatabase.class.getSimpleName();
    public static final Object LOCK=new Object();
    public static final String DATABASE_NAME="todolist";
    public static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        //checking it whether null or not so that database could be created once only
        if(sInstance==null)
        {
            Log.d(LOG_TAG,"creating database");
            sInstance= Room.databaseBuilder(context.getApplicationContext()
                    ,AppDatabase.class,AppDatabase.DATABASE_NAME)
                    //queries should be done on a seperate thread to avoid locking the UI
                    //WE allow this only temporarily to see if database is working
                    //.allowMainThreadQueries()
                    .build();
        }
        //gettign the already created database
        Log.d(LOG_TAG,"getting database");
        return sInstance;

    }
    public  abstract TaskDao taskDao();
    /*
    * Accessing the database in the main thread can be time consuming, and could lock the UI and throw an Application Not Responding error. To avoid that, Room will, by default, throw an error if you attempt to access the database in the main thread.
In the finished app, we need to implement the database operations to run asynchronously, but we also need to validate that what we have done so far is working, so let’s temporarily enable the option to allow queries in the main thread.*/
}
