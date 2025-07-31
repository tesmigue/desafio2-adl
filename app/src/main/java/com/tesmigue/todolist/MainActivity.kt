package com.tesmigue.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesmigue.todolist.adapter.TasksAdapter
import com.tesmigue.todolist.databinding.ActivityMainBinding
import com.tesmigue.todolist.model.Task
import com.tesmigue.todolist.utils.SwipeToDeleteCallback
import android.text.Editable
import android.text.TextWatcher

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TasksAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = TasksAdapter(taskList,
            onCheckChanged = { updatePendingCount() },
            onEditClicked = { task -> editTask(task) },
            onDeleteClicked = { task -> deleteTask(task) }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        val swipeToDelete = SwipeToDeleteCallback { position ->
            val task = taskList[position]
            deleteTask(task)
        }

        ItemTouchHelper(swipeToDelete).attachToRecyclerView(binding.rvTasks)
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            val input = binding.etTask.text.toString().trim()
            if (input.isNotEmpty()) {
                val newTask = Task(title = input)
                adapter.addTask(newTask)
                binding.etTask.text.clear()
                updatePendingCount()
            } else {
                Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnAdd.isEnabled = s.toString().trim().isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun editTask(task: Task) {
        binding.etTask.setText(task.title)
        deleteTask(task)
        binding.etTask.requestFocus()
    }

    private fun deleteTask(task: Task) {
        adapter.removeTask(task)
        updatePendingCount()
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
    }

    private fun updatePendingCount() {
        val count = taskList.count { !it.isDone }
        binding.tvCounter.text = "Pending tasks: $count"
    }
}
