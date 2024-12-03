package com.example.todolist

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var todoAdapter: TodoAdapter
    private val allTodos = mutableListOf<Todo>() // All tasks
    private var currentFontSize = 18f
    private var currentCategory: TodoCategory? = null
    private var currentPriority: TodoPriority? = null
    private var selectedReminderTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide ActionBar
        supportActionBar?.hide()

        // Initialize RecyclerView
        todoAdapter = TodoAdapter(mutableListOf())
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        // Setup category and priority spinners
        setupCategorySpinner()
        setupPrioritySpinner()

        // Add task button
        btnAddTodo.setOnClickListener {
            addTodo()
        }

        // Delete completed tasks button
        btnDeleteDoneTodo.setOnClickListener {
            deleteDoneTodos()
        }

        // Search bar text listener
        etSearchTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Adjust font size button
        btnAdjustFontSize.setOnClickListener {
            adjustGlobalFontSize()
        }

        // Set reminder checkbox
        checkBoxReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDateTimePickerDialog()
            } else {
                selectedReminderTime = null // Clear reminder time
            }
        }

        // Set up user profile button
        val iconUserProfile = findViewById<ImageView>(R.id.iconUserProfile)
        iconUserProfile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Setup category spinner
     */
    private fun setupCategorySpinner() {
        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.todo_categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = if (position > 0) TodoCategory.values()[position - 1] else null
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currentCategory = null
                applyFilters()
            }
        }
    }

    /**
     * Setup priority spinner
     */
    private fun setupPrioritySpinner() {
        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.todo_priorities,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter
        spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentPriority = if (position > 0) TodoPriority.values()[position - 1] else null
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currentPriority = null
                applyFilters()
            }
        }
    }

    /**
     * Add a new task
     */
    private fun addTodo() {
        val todoTitle = etTodoTitle.text.toString().trim()

        if (todoTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a valid task title", Toast.LENGTH_SHORT).show()
            return
        }

        val category = currentCategory ?: TodoCategory.PERSONAL
        val priority = currentPriority ?: TodoPriority.LOW

        val todo = Todo(
            title = todoTitle,
            category = category,
            priority = priority,
            reminderTime = selectedReminderTime
        )

        if (checkBoxReminder.isChecked && selectedReminderTime != null) {
            setReminder(todo)
        }

        allTodos.add(todo)
        todoAdapter.addTodo(todo)
        etTodoTitle.text.clear()
        selectedReminderTime = null
    }

    /**
     * Delete completed tasks
     */
    private fun deleteDoneTodos() {
        allTodos.removeAll { it.isChecked }
        todoAdapter.deleteDoneTodos()
    }

    /**
     * Filter tasks
     */
    private fun applyFilters(query: String = "") {
        var filteredTodos = if (query.isEmpty()) {
            allTodos
        } else {
            allTodos.filter { it.title.contains(query, ignoreCase = true) }
        }

        currentCategory?.let { category ->
            filteredTodos = filteredTodos.filter { it.category == category }
        }

        currentPriority?.let { priority ->
            filteredTodos = filteredTodos.filter { it.priority == priority }
        }

        todoAdapter.updateTodos(filteredTodos)
    }

    /**
     * Show date and time picker dialog
     */
    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()

        // Date picker
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Time picker
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                selectedReminderTime = calendar.timeInMillis
                val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
                Toast.makeText(this, "Reminder set for $formattedTime", Toast.LENGTH_SHORT).show()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePickerDialog.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    /**
     * Set a task reminder
     */
    private fun setReminder(todo: Todo) {
        selectedReminderTime?.let { reminderTime ->
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
                putExtra("TODO_TITLE", todo.title)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                todo.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
            val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(reminderTime)
            Toast.makeText(this, "Reminder is set for $formattedTime", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adjust global font size
     */
    private fun adjustGlobalFontSize() {
        currentFontSize += 2f
        if (currentFontSize > 30f) {
            currentFontSize = 18f
        }

        etSearchTodo.textSize = currentFontSize
        todoAdapter.setGlobalFontSize(currentFontSize)
    }
}
