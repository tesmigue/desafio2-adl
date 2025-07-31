package com.tesmigue.todolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tesmigue.todolist.R
import com.tesmigue.todolist.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(
    private val tasks: MutableList<Task>,
    private val onCheckChanged: () -> Unit,
    private val onEditClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    fun addTask(task: Task) {
        tasks.add(0, task)
        notifyItemInserted(0)
    }

    fun removeTask(task: Task) {
        val position = tasks.indexOf(task)
        tasks.remove(task)
        notifyItemRemoved(position)
    }

    fun updateList(newTasks: MutableList<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cbTitle: CheckBox = view.findViewById(R.id.cbTitle)
        private val tvDate: TextView = view.findViewById(R.id.tvDate)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(task: Task) {
            cbTitle.setOnCheckedChangeListener(null)
            cbTitle.text = task.title
            cbTitle.isChecked = task.isDone

            val date = Date(task.creationDate)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvDate.text = formatter.format(date)

            cbTitle.setOnCheckedChangeListener { _, isChecked ->
                task.isDone = isChecked
                onCheckChanged()
            }

            btnDelete.setOnClickListener {
                onDeleteClicked(task)
            }

            cbTitle.setOnLongClickListener {
                onEditClicked(task)
                true
            }
        }
    }
}
