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




