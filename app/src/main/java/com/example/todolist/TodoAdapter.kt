package com.example.todolist

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

class TodoAdapter(
    private val todos: MutableList<Todo> // manage all tasks
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var fontSize: Float = 18f


    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTodoTitle: TextView = itemView.findViewById(R.id.tvTodoTitle) //task tittle
        val spinnerCategory: Spinner = itemView.findViewById(R.id.spinnerCategory) // category chosen
        val spinnerPriority: Spinner = itemView.findViewById(R.id.spinnerPriority) // priority chosen
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone) // complete or not
    }

    /**
     * create new ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    /**
     * competed task delete line
     */
    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        tvTodoTitle.paintFlags = if (isChecked) {
            tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    /**
     * add new tasks
     */
    fun addTodo(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }

    /**
     * delete done tasks
     */
    fun deleteDoneTodos() {
        todos.removeAll { it.isChecked }
        notifyDataSetChanged()
    }

    /**
     * font size
     */
    fun setGlobalFontSize(size: Float) {
        fontSize = size
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        with(holder) {

            tvTodoTitle.text = curTodo.title
            tvTodoTitle.textSize = fontSize

            // setup category Spinner
            val categoryAdapter = ArrayAdapter(
                itemView.context,
                android.R.layout.simple_spinner_item,
                TodoCategory.values().map { it.name }
            )
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = categoryAdapter
            spinnerCategory.setSelection(curTodo.category.ordinal)


            spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    curTodo.category = TodoCategory.values()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // setup priority Spinner
            val priorityAdapter = ArrayAdapter(
                itemView.context,
                android.R.layout.simple_spinner_item,
                TodoPriority.values().map { it.name }
            )
            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPriority.adapter = priorityAdapter
            spinnerPriority.setSelection(curTodo.priority.ordinal)


            spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    curTodo.priority = TodoPriority.values()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


            cbDone.isChecked = curTodo.isChecked
            toggleStrikeThrough(tvTodoTitle, curTodo.isChecked)


            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                curTodo.isChecked = isChecked
            }
        }
    }


    override fun getItemCount(): Int = todos.size


    fun getTodos(): List<Todo> = todos


    fun updateTodos(newTodos: List<Todo>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()
    }
}
