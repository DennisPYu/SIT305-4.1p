/* 

// DB Helper class 


package com.example.task41;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TASK_TABLE = "task_table";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ID = "ID";

    public DBHelper(Context context) {
        super(context, "Taskmanager.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TASK_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTableStatement);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }


    public boolean addOne(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESCRIPTION, task.getDescription());
        cv.put(COLUMN_DATE, task.getDate());
        long insert = db.insert(TASK_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    public boolean deleteOne(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteCount = db.delete(TASK_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return deleteCount > 0;
    }

    public boolean editTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESCRIPTION, task.getDescription());
        cv.put(COLUMN_DATE, task.getDate());
        int updateCount = db.update(TASK_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return updateCount > 0;
    }


    public List<TaskModel> getAll() {
        List<TaskModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + TASK_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int taskID = cursor.getInt(0);
                String taskTitle = cursor.getString(1);
                String taskDescription = cursor.getString(2);
                String taskDate = cursor.getString(3);
                TaskModel newTask = new TaskModel(taskID, taskTitle, taskDescription, taskDate);
                returnList.add(newTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

}




//EditTaskActivity.java

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




//MainActivity.java

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




//TaskModel.java
package com.example.task41;

import java.io.Serializable;

public class TaskModel implements Serializable {

    private int id;
    private String title;
    private String description;
    private String date;


    public TaskModel(int id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }


    @Override
    public String toString() {
        return "TaskModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}





//TasksAdapter.java

package com.example.task41;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    private Context context;
    private List<TaskModel> tasks;
    private TaskInteractionListener listener;

    public TasksAdapter(Context context, TaskInteractionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface TaskInteractionListener {
        void onEditTask(TaskModel task);

        void onDeleteTask(TaskModel task);
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = tasks.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.dateTextView.setText(task.getDate());

        holder.itemView.setOnLongClickListener(view -> {
            showOptions(task);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }


    private void showOptions(TaskModel task) {
        CharSequence[] items = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option");
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    listener.onEditTask(task);
                    break;
                case 1:
                    listener.onDeleteTask(task);
                    break;
            }
        });
        builder.show();
    }


    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView;

        TaskViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            descriptionTextView = itemView.findViewById(R.id.description);
            dateTextView = itemView.findViewById(R.id.date);
        }
    }

}










*/