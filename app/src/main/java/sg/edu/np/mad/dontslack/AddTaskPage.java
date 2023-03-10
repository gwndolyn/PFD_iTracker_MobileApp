package sg.edu.np.mad.dontslack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskPage extends AppCompatActivity {
    private final String TAG = "Add Task";
    DBHandler dbHandler = new DBHandler(this,null,null,1);
    private static String etTaskTitle;
    private static String etTaskDescription;
    private static String etTaskStartTIme;
    private static String etTaskDeadLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_page);
        /* Hiding the top bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //Getting bundle from previous activity
        Bundle categoryBundle = getIntent().getExtras();
        String taskCategory = categoryBundle.getString("category");

        //Back button
        ImageView goBackButton = findViewById(R.id.backHome2);
        goBackButton.setOnClickListener(v -> {
            Intent myIntent2 = new Intent(AddTaskPage.this, ToDoList.class);
            startActivity(myIntent2);
        });


        @SuppressLint("CutPasteId") EditText taskStartTime = findViewById(R.id.editCurrentTaskStartTime);
        taskStartTime.setOnClickListener(v -> showDateTimeDialog(taskStartTime));

        @SuppressLint("CutPasteId") EditText taskDeadLine = findViewById(R.id.editCurrentTaskDeadline);
        taskDeadLine.setOnClickListener(v -> showDateTimeDialog(taskDeadLine));

        //Creating task button
        Button createTaskButton = findViewById(R.id.saveEditTaskButton);
        createTaskButton.setOnClickListener(v -> {
            EditText newTaskTitle = findViewById(R.id.editCurrentTaskTitle);
            etTaskTitle = newTaskTitle.getText().toString();
            EditText newTaskDescription = findViewById(R.id.editCurrentTaskDescription);
            etTaskDescription = newTaskDescription.getText().toString();
            @SuppressLint("CutPasteId") EditText newTaskStartTime = findViewById(R.id.editCurrentTaskStartTime);
            etTaskStartTIme = newTaskStartTime.getText().toString();
            @SuppressLint("CutPasteId") EditText newTaskDeadLine = findViewById(R.id.editCurrentTaskDeadline);
            etTaskDeadLine = newTaskDeadLine.getText().toString();
            TaskObject ifTaskExist = dbHandler.findTask(etTaskTitle);
            //Check is the task exist
            if(ifTaskExist == null){
                //if not exist check if all fields are filled
                if(etTaskTitle.equals("") || etTaskDescription.equals("") || etTaskStartTIme.equals("") || etTaskDeadLine.equals("")){
                    Toast.makeText(AddTaskPage.this,"Please ensure all fields are filled.",Toast.LENGTH_SHORT).show();
                }
                else{
                    TaskObject newTaskObject = new TaskObject();
                    newTaskObject.setTaskCategory(taskCategory);
                    newTaskObject.setTaskName(etTaskTitle);
                    newTaskObject.setTaskDescription(etTaskDescription);
                    newTaskObject.setTaskStartTime(etTaskStartTIme);
                    newTaskObject.setTaskDeadLine(etTaskDeadLine);
                    newTaskObject.setTaskStatus(false);
                    dbHandler.addTask(newTaskObject);
                    Toast.makeText(AddTaskPage.this,"Task Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(AddTaskPage.this,ToDoList.class);
                    startActivity(myIntent);
                }
            }
            else{
                Toast.makeText(AddTaskPage.this,"Task already exist!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDateTimeDialog(EditText date_time){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm aaa");
                date_time.setText(simpleDateFormat.format(calendar.getTime()));
            };
            new TimePickerDialog(AddTaskPage.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
        };
        new DatePickerDialog(AddTaskPage.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}