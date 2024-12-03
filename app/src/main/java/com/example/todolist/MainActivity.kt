package com.example.todolist

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

        // hide ActionBar
        supportActionBar?.hide()

        // initial RecyclerView
        todoAdapter = TodoAdapter(mutableListOf())
        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        // Category Spinner
        setupCategorySpinner()

        // priority Spinner
        setupPrioritySpinner()

        // Add button
        btnAddTodo.setOnClickListener {
            addTodo()
        }

        // Delete button
        btnDeleteDoneTodo.setOnClickListener {
            deleteDoneTodos()
        }

        // search box
        etSearchTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Change font size
        btnAdjustFontSize.setOnClickListener {
            adjustGlobalFontSize()
        }

        // Set Reminder CheckBox
        checkBoxReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDateTimePickerDialog()
            } else {
                selectedReminderTime = null // clear reminder time
            }
        }
    }

    /**
     * initial category Spinner
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
     * initial priority Spinner
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
     * add new tasks
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
            reminderTime = selectedReminderTime // 保存用户选择的日期时间
        )

        if (checkBoxReminder.isChecked && selectedReminderTime != null) {
            setReminder(todo)
        }

        allTodos.add(todo)
        todoAdapter.addTodo(todo)
        etTodoTitle.text.clear()
        selectedReminderTime = null // Clear reminder time
    }

    /**
     * Delete completed tasks
     */
    private fun deleteDoneTodos() {
        allTodos.removeAll { it.isChecked }
        todoAdapter.deleteDoneTodos()
    }

    /**
     * filter tasks
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
     * Show selection of time and date
     */
    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()

        // date selection
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            // set up chosen date
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // time selection
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                // setup chosen time
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
     * setup task reminder
     */
    private fun setReminder(todo: Todo) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 1) // Set the alarm for 1 minute ahead
        selectedReminderTime = calendar.timeInMillis // Update the selected reminder time

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

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )

            val formattedTime = Todo.formattedReminderTime(reminderTime)
            Toast.makeText(this, "Reminder is set for $formattedTime", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * change font size
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
