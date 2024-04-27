package com.example.task41;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TasksAdapter.TaskInteractionListener {

    private EditText editTextTitle, editTextDescription, editTextDate;
    private Button addButton, sortButton;
    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;
    private DBHelper dbHelper;
    private boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.et_date);
        editTextDate.setOnClickListener(v -> showDatePicker());
        editTextTitle = findViewById(R.id.et_title);
        editTextDescription = findViewById(R.id.et_description);
        addButton = findViewById(R.id.btn_add);
        sortButton = findViewById(R.id.btn_sort);
        tasksRecyclerView = findViewById(R.id.task_list);

        dbHelper = new DBHelper(this);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(this, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        updateTasksList();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortTasks();
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                editTextDate.setText(formattedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void updateTasksList() {
        List<TaskModel> tasksList = dbHelper.getAll();
        tasksAdapter.setTasks(tasksList);
    }

    private void addTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String date = editTextDate.getText().toString();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskModel task = new TaskModel(0, title, description, date);
        dbHelper.addOne(task);
        updateTasksList();
    }

    private void sortTasks() {
        List<TaskModel> tasksList = tasksAdapter.getTasks();
        if (tasksList != null) {
            if (sortAscending) {
                Collections.sort(tasksList, (task1, task2) -> task1.getDate().compareTo(task2.getDate()));
            } else {
                Collections.sort(tasksList, (task1, task2) -> task2.getDate().compareTo(task1.getDate()));
            }

            tasksAdapter.notifyDataSetChanged();
            tasksRecyclerView.scrollToPosition(0);
            sortAscending = !sortAscending;
        }
    }


    @Override
    public void onEditTask(TaskModel task) {
        Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
        intent.putExtra("task", task);
        startActivity(intent);
    }

    @Override
    public void onDeleteTask(TaskModel task) {
        dbHelper.deleteOne(task);
        updateTasksList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateTasksList();
    }

}




