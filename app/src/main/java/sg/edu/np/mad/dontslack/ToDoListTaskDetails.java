package sg.edu.np.mad.dontslack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Locale;

public class ToDoListTaskDetails extends AppCompatActivity {
    private final String TAG = "Task Detail";
    private int seconds;
    private boolean running;
    private boolean wasRunning;
    private boolean timerStarted = false;
    private boolean isTimerStarted = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_task_details);
        DBHandler dbHandler = new DBHandler(this,null,null,1);

        /* Hiding the top bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //Setting up all the textview and button
        Bundle taskBundle = getIntent().getBundleExtra("Bundle");
        TaskObject currentTask = (TaskObject) taskBundle.getSerializable("TaskObject");

        TextView taskTitle = findViewById(R.id.taskTitle);
        taskTitle.setText(currentTask.getTaskName());

        TextView taskDescription = findViewById(R.id.taskDiscription);
        taskDescription.setText(currentTask.getTaskDescription());

        TextView taskStartTime = findViewById(R.id.taskStartTIme);
        taskStartTime.setText(currentTask.getTaskStartTime());

        TextView taskDeadLine = findViewById(R.id.tasktDeadLine);
        taskDeadLine.setText(currentTask.getTaskDeadLine());

        ImageView backButton = findViewById(R.id.taskDetailBackButton);
        backButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(ToDoListTaskDetails.this,ToDoList.class);
            startActivity(myIntent);
        });

        //This is the edit button allow user to edit the detail of the task
        TextView editTaskButton = findViewById(R.id.editTaskButton);
        editTaskButton.setOnClickListener(v -> {
            Intent myIntent = new Intent (ToDoListTaskDetails.this, ToDOListEditTaskDetails.class);
            Bundle args = new Bundle();
            args.putSerializable("TaskObject", (Serializable) currentTask);
            myIntent.putExtra("Bundle",args);
            startActivity(myIntent);
        });

        //Delete current task button
        TextView deleteTaskButton = findViewById(R.id.deleteTaskButton);
        deleteTaskButton.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Delete Task");
            alertDialog.setMessage("Are you sure you want to delete this task?");

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> dialog.dismiss());

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes, I'm sure.", (dialog, which) -> {
                dbHandler.deleteTask(currentTask);
                Toast.makeText(this,"Task deleted successfully!",Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(this,ToDoList.class);
                startActivity(myIntent);
            });
            alertDialog.show();
        });
        //This is the button to trigger to timer
        Button startStopButton = findViewById(R.id.startStopTaskButton);
        startStopButton.setOnClickListener(v -> {
            if(savedInstanceState != null){
                savedInstanceState.getInt("seconds");
                savedInstanceState.getBoolean("running");
                savedInstanceState.getBoolean("wasRunning");

            }
            if(!isTimerStarted){
                isTimerStarted = true;
                runTimer();
            }
            if(timerStarted){
                startStopButton.setText("Resume");
                timerStarted = false;
                onStop();
            }
            else{
                startStopButton.setText("Stop");
                timerStarted = true;
                onStart();
            }

        });
        //If user can set the task status to complete by pressing this complete button
        Button completeTaskButton = findViewById(R.id.completeTaskButton);
        if(currentTask.isTaskStatus()){
            completeTaskButton.setText("Completed");
            completeTaskButton.setTextColor(Color.rgb(169, 169, 169));
        }
        //Listener for complete task button
        completeTaskButton.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(ToDoListTaskDetails.this).create();
            //If the current task is completed
            if(currentTask.isTaskStatus()){
                //Check is the user want to revert the task status
                alertDialog.setTitle("Task Completed");
                alertDialog.setMessage("Are you sure you want to revert the status");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Back", (dialog, which) -> dialog.dismiss());

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes, I'm sure.", (dialog, which) -> {
                    currentTask.setTaskStatus(false);
                    dbHandler.updateTaskData(currentTask,currentTask.getTaskName());
                    dialog.dismiss();
                    completeTaskButton.setText("Complete");
                    completeTaskButton.setTextColor(Color.rgb(255,255,255));
                    Log.v(TAG,"TaskStatus"+currentTask.isTaskStatus());
                });
            }
            else{
                //Else check if the user is sure the task is completed
                alertDialog.setTitle("Task Completion");
                alertDialog.setMessage("Are you sure you have completed the task?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Back to Work", (dialog, which) -> dialog.dismiss());

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes, I'm sure.", (dialog, which) -> {
                    currentTask.setTaskStatus(true);
                    dbHandler.updateTaskData(currentTask,currentTask.getTaskName());
                    dialog.dismiss();
                    onStop();
                    completeTaskButton.setText("Completed");
                    completeTaskButton.setTextColor(Color.rgb(169, 169, 169));
                    Toast.makeText(ToDoListTaskDetails.this,"Nice Job! You have completed the task!",Toast.LENGTH_SHORT).show();
                });
            }
            alertDialog.show();


        });
    }

    //From this onwards are the methods for timer
    public void onStart() {
        super.onStart();
        running = true;
    }

    public void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    public void onPause(){
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(wasRunning){
            running = true;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);

    }

    public void runTimer(){
        TextView timeView = findViewById(R.id.taskTimer);
        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d : %02d : %02d", hours, minutes, secs);
                timeView.setText(time);

                if (running){
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }

        });
    }





}