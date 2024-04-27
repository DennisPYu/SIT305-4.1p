package com.example.task41;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextDate;
    private Button buttonSave, buttonCancel;
    private TaskModel task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_layout);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        task = (TaskModel) getIntent().getSerializableExtra("task");
        if (task != null) {
            editTextTitle.setText(task.getTitle());
            editTextDescription.setText(task.getDescription());
            editTextDate.setText(task.getDate());
        }

        editTextDate.setOnClickListener(view -> showDatePicker());

        buttonSave.setOnClickListener(view -> updateTask());
        buttonCancel.setOnClickListener(view -> finish());
    }

    private void updateTask() {
        String updatedTitle = editTextTitle.getText().toString();
        String updatedDescription = editTextDescription.getText().toString();
        String updatedDate = editTextDate.getText().toString();

        if (updatedTitle.trim().isEmpty() || updatedDescription.trim().isEmpty() || updatedDate.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        task.setTitle(updatedTitle);
        task.setDescription(updatedDescription);
        task.setDate(updatedDate);
        DBHelper dbHelper = new DBHelper(this);
        boolean isUpdated = dbHelper.editTask(task);

        if (isUpdated) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    editTextDate.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }


}




