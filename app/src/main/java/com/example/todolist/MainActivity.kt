package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private val allTodos = mutableListOf<Todo>() // 存储所有待办事项
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
                allTodos.add(todo) // 添加到所有待办事项列表
                todoAdapter.addTodo(todo) // 添加到当前显示的适配器中
                etTodoTitle.text.clear()
            }
        }

        // 删除已完成的待办事项
        btnDeleteDoneTodo.setOnClickListener {
            allTodos.removeAll { it.isDone } // 从完整列表中移除已完成的项
            todoAdapter.deleteDoneTodos() // 更新适配器显示
        }

        // 搜索功能
        etSearchTodo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTodos(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 调节全局字体大小
        btnAdjustFontSize.setOnClickListener {
            adjustGlobalFontSize()
        }
    }

    /**
     * 根据搜索条件过滤待办事项
     */
    private fun filterTodos(query: String) {
        val filteredTodos = if (query.isEmpty()) {
            allTodos // 如果搜索框为空，显示所有待办事项
        } else {
            allTodos.filter { it.title.contains(query, ignoreCase = true) } // 过滤包含关键字的待办事项
        }
        todoAdapter.updateTodos(filteredTodos) // 更新适配器显示内容
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
        tvTodoListTitle.textSize = currentFontSize
        tvWorkTitle.textSize = currentFontSize

        // 设置按钮字体大小
        btnAddTodo.textSize = currentFontSize
        btnDeleteDoneTodo.textSize = currentFontSize

        // 更新适配器以调整待办事项字体
        todoAdapter.setGlobalFontSize(currentFontSize)
    }
}
