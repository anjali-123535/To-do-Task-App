package com.example.to_do;

import androidx.appcompat.app.AppCompatActivity;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.to_do.database.AppDatabase;
import com.example.to_do.database.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the adapter and attach it to the RecyclerVie
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        List<TaskEntry> taskEntries=mAdapter.getTasks();
                        int position=viewHolder.getAdapterPosition();
                        //this will delete the entry but UI wil not be updated
                        db.taskDao().delete(taskEntries.get(position));
                        /*this will update yhe UI
                        retrieveTasks();*/
                        //now no need to call the reterieveTaks since the onchanged of the livedata will be called everytime theeir is any change in the database

                    }
                });
                // Here is where you'll implement swipe to delete
            }
        }).attachToRecyclerView(mRecyclerView);
        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be create
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });
        db=AppDatabase.getInstance(getApplicationContext());
        //we could have quried the databse in the onCreate itself but then it will never be refereshed unless the activity is recreated
        //so better do it in onResume()

Log.d(TAG,"in oncreate");
        setUpViewModel();

    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent=new Intent(MainActivity.this,AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID,itemId);
        startActivity(intent);
        // Launch AddTaskActivity adding the itemId as an extra in the intent
    }
//this will be calledx when this activity will be restarted or paused
    @Override
    protected void onResume() {
        //this way  every time we come back to our main activity after adding the new tasks we aould see the newly added task in the list
        //mAdapter.setTasks(db.taskDao().loadAllTaks());
        super.onResume();
        //in this mehtod we are querying the database everytime  in order to check for the change
        Log.d(TAG,"in on resume");
            }

    private void setUpViewModel() {

       /* AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
            //out of the main thread
                Log.d("MAIN ACTIVTY","activity retrieving the task from the database");
                final List<TaskEntry> tasks=db.taskDao().loadAllTaks();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    //on the main thread
                        mAdapter.setTasks(tasks);
                    }
                });
            }
        //});
    //}
}*/


        //LiveData will run by default on mainThread
        // we need to observe the changes ,and this will happen only in the query methosd so it can come out of the Executor
        //we are using livedata to observe the changes in the database
        //for other methods such as insert delete update we do not observe the changes so we will not be using the LiveData

        Log.d(TAG,"Actively retrieving the tasks from the database");
// LiveData runs out of the main thread
        //now we dont want this loading herer since we will bw getting the data from the viewmodel everytime the mobile rotatwes
   //  final LiveData<List<TaskEntry>> tasks =db.taskDao().loadAllTaks();
        MainViewModel mv= ViewModelProviders.of(this).get(MainViewModel.class);
        Log.d(TAG,"getting data from viewmodel");
          mv.getLiveData().observe(this,
                  new Observer<List<TaskEntry>>() {
    @Override
    public void onChanged(List<TaskEntry> taskEntries) {
        //onChanged runs on thje main thread
        //every change in the database wiil trigger the onChanged method of the the LIveData
        Log.d(TAG,"updating list of tasks from the livedata in the viewmodel");
        mAdapter.setTasks(taskEntries);
    }
});

    }
}