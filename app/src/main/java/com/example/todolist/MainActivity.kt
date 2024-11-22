package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private var currentFontSize = 18f // 默认字体大小

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 隐藏 ActionBar
        supportActionBar?.hide()

        // 初始化适配器
        todoAdapter = TodoAdapter(mutableListOf())

        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)


        // 添加待办事项
        btnAddTodo.setOnClickListener {
            val todoTitle = etTodoTitle.text.toString()
            if (todoTitle.isNotEmpty()) {
                val todo = Todo(todoTitle)
                todoAdapter.addTodo(todo)
                etTodoTitle.text.clear()
            }
        }

        // 删除已完成的待办事项
        btnDeleteDoneTodo.setOnClickListener {
            todoAdapter.deleteDoneTodos()
        }

        // 搜索功能
        etSearchTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredTodos = todoAdapter.getTodos().filter {
                    it.title.contains(s.toString(), ignoreCase = true)
                }
                todoAdapter.updateTodos(filteredTodos)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 调节全局字体大小
        btnAdjustFontSize.setOnClickListener {
            adjustGlobalFontSize()
        }
    }

    /**
     * 调节全局字体大小
     */
    private fun adjustGlobalFontSize() {
        // 增大字体大小
        currentFontSize += 2f
        if (currentFontSize > 30f) {
            currentFontSize = 18f // 超过最大值时重置为默认大小
        }

        // 设置搜索框字体大小
        etSearchTodo.textSize = currentFontSize

        // 设置标题字体大小
        if (tvTodoListTitle is TextView) {
            (tvTodoListTitle as TextView).textSize = currentFontSize
        } else {
            // 如果不是 TextView，可以选择跳过或者处理异常情况
            println("tvTodoListTitle is not a TextView")
        }

        supportActionBar?.title = "TodoList"
        val toolbarTitle = findViewById<TextView>(R.id.tvTodoListTitle) // 自定义标题
        toolbarTitle?.textSize = currentFontSize



        // 设置按钮字体大小
        btnAddTodo.textSize = currentFontSize
        btnDeleteDoneTodo.textSize = currentFontSize

        // 更新适配器以调整待办事项字体
        todoAdapter.setGlobalFontSize(currentFontSize)
    }
}
