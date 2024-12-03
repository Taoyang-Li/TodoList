package com.example.todolist

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 数据类表示一个待办事项（Todo）
 */
data class Todo(
    val title: String,                // 待办事项标题
    var isChecked: Boolean = false,  // 是否标记为完成
    var category: TodoCategory = TodoCategory.PERSONAL, // 任务分类
    var priority: TodoPriority = TodoPriority.LOW,      // 任务优先级
    var reminderTime: Long? = null   // 提醒时间（时间戳，毫秒）
) {
    /**
     * 实例方法：格式化提醒时间
     * 返回格式为 yyyy-MM-dd HH:mm 的字符串，如果没有提醒时间则返回 null
     */
    fun formattedReminderTime(): String? {
        return reminderTime?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(it))
        }
    }

    /**
     * 实例方法：检查任务是否有提醒时间
     */
    fun hasReminder(): Boolean {
        return reminderTime != null
    }

    companion object {
        /**
         * 静态方法：格式化给定的提醒时间戳
         * @param reminderTime 时间戳（毫秒）
         * @return 格式化的时间字符串，格式为 yyyy-MM-dd HH:mm
         */
        fun formattedReminderTime(reminderTime: Long?): String? {
            return reminderTime?.let {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(it))
            }
        }
    }
}

/**
 * 枚举类定义待办事项的分类
 */
enum class TodoCategory {
    PERSONAL,  // 个人事务
    WORK,      // 工作事务
    URGENT,    // 紧急事务
    SHOPPING,  // 购物清单
    OTHER      // 其他类型
}

/**
 * 枚举类定义待办事项的优先级
 */
enum class TodoPriority {
    LOW,    // 低优先级
    MEDIUM, // 中优先级
    HIGH    // 高优先级
}
