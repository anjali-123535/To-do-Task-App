package com.example.to_do;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.to_do.database.AppDatabase;
import com.example.to_do.database.TaskDao;
import com.example.to_do.database.TaskEntry;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    private int mTaskId = DEFAULT_TASK_ID;
    // TODO (3) Create AppDatabase member variable for the Database
    private AppDatabase db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Log.d(TAG,"onCreate called");
        initViews();
        // TODO (4) Initialize member variable for the data base
        db=AppDatabase.getInstance(getApplicationContext());
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId=intent.getIntExtra(EXTRA_TASK_ID,DEFAULT_TASK_ID);
               /* AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final TaskEntry taskEntry=db.taskDao().loadTaskById(mTaskId);
                        //we can not populate the UI THE onn this thread
                        //we will be able tomsolve this one we learn the android architecture components
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateUI(taskEntry);
                            }
                        });
                    }
                });*/
                Log.d(TAG,"loading the data of a single task");
                final LiveData<TaskEntry> taskEntry=db.taskDao().loadTaskById(mTaskId);
                taskEntry.observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(TaskEntry taskEntry) {
                        Log.d(TAG,"populate the UI");
                        populateUI(taskEntry);
                    }
                });
                // populate the UI
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }
    /**
     * initViews is called from onCreate to init the member variable view
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);
        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {

mEditText.setText(task.getDescription());
setPriorityInViews(task.getPriority());
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */

    public void onSaveButtonClicked() {

        // TODO (5) Create a description variable and assign to it the value in the edit text
        String description=mEditText.getText().toString();
        int priority=getPriorityFromViews();

        // TODO (6) Create a priority variable and assign the value returned by getPriorityFromViews()

        // TODO (7) Create a date variable and assign to it the current Date
        Date date=new Date();
        final TaskEntry taskEntry=new TaskEntry(description,priority,date);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mTaskId==DEFAULT_TASK_ID) {
                    db.taskDao().insertTask(taskEntry);
                }
                else
                {
                    taskEntry.setId(mTaskId);
                    db.taskDao().update(taskEntry);
                }
                finish();
            }
        });


        // TODO (8) Create taskEntry variable using the variables defined above
        // TODO (9) Use the taskDao in the AppDatabase variable to insert the taskEntry
        // TODO (10) call finish() to come back to MainActivity
    }
    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }
    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);

        }
    }
}