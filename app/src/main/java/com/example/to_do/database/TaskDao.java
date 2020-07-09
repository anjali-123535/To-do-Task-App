package com.example.to_do.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task ORDER BY priority")
   LiveData<List<TaskEntry>> loadAllTaks();

    @Insert()
    void insertTask(TaskEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(TaskEntry taskEntry);

    @Delete()
    void delete(TaskEntry taskEntry);
    @Query("select * from task where id= :id")
    LiveData<TaskEntry> loadTaskById(int id);
}
