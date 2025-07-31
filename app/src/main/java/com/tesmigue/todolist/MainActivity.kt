package com.tesmigue.todolist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tesmigue.todolist.adapter.TasksAdapter
import com.tesmigue.todolist.databinding.ActivityMainBinding
import com.tesmigue.todolist.model.Task
import com.tesmigue.todolist.utils.SwipeToDeleteCallback

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TasksAdapter
    private var taskBeingEdited: Task? = null
    private val allTasks = mutableListOf<Task>()
    private var filterMode: FilterMode = FilterMode.ALL

    enum class FilterMode { ALL, PENDING, DONE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTasks()
        setupRecyclerView()
        setupListeners()
        bindFilterButtons()
        applyFilter()
    }

    private fun loadTasks() {
        val prefs = getSharedPreferences("task_prefs", MODE_PRIVATE)
        val json = prefs.getString("tasks", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            allTasks.addAll(Gson().fromJson(json, type))
        }
    }

    private fun saveTasks() {
        val prefs = getSharedPreferences("task_prefs", MODE_PRIVATE)
        val json = Gson().toJson(allTasks)
        prefs.edit().putString("tasks", json).apply()
    }

    private fun setupRecyclerView() {
        adapter = TasksAdapter(mutableListOf(), { updateCount(); saveTasks() }, this::editTask, this::deleteTask)
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        ItemTouchHelper(SwipeToDeleteCallback { pos ->
            deleteTask(allTasks[pos])
        }).attachToRecyclerView(binding.rvTasks)
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            val input = binding.etTask.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (taskBeingEdited != null) {
                taskBeingEdited!!.title = input
                taskBeingEdited = null
                binding.btnAdd.text = "Add Task"
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
            } else {
                val t = Task(title = input)
                allTasks.add(0, t)
            }
            binding.etTask.text.clear()
            updateCount()
            saveTasks()
            applyFilter()
        }

        binding.etTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                binding.btnAdd.isEnabled = s.toString().trim().isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun bindFilterButtons() {
        binding.btnFilterAll.setOnClickListener { filterMode = FilterMode.ALL; applyFilter() }
        binding.btnFilterPending.setOnClickListener { filterMode = FilterMode.PENDING; applyFilter() }
        binding.btnFilterDone.setOnClickListener { filterMode = FilterMode.DONE; applyFilter() }
    }

    private fun applyFilter() {
        val filtered = when (filterMode) {
            FilterMode.PENDING -> allTasks.filter { !it.isDone }
            FilterMode.DONE -> allTasks.filter { it.isDone }
            else -> allTasks
        }
        adapter.updateList(filtered.toMutableList())
        updateCount()
    }

    private fun updateCount() {
        val pending = allTasks.count { !it.isDone }
        binding.tvCounter.text = "Pending tasks: $pending"
    }

    private fun deleteTask(task: Task) {
        allTasks.remove(task)
        applyFilter()
        updateCount()
        saveTasks()
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun editTask(task: Task) {
        binding.etTask.setText(task.title)
        taskBeingEdited = task
        binding.btnAdd.text = "Update Task"
    }
}
