package com.swpp10.calendy.data.maindb.rawplan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swpp10.calendy.data.maindb.plan.Todo
import java.util.Date

// table name tod0(=t0do) in 'calendy_database.db'
@Entity(
    tableName = "raw_todo"
)
data class RawTodo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    override val title: String,
    @ColumnInfo(name = "due_time")
    val dueTime: Date,
    @ColumnInfo(name = "complete", defaultValue = "0")
    val complete: Boolean = false,
    @ColumnInfo(name = "memo", defaultValue = "")
    override val memo: String = "",
    @ColumnInfo(name = "repeat_group_id", defaultValue = "NULL")
    override val repeatGroupId: Int? = null,
    @ColumnInfo(name = "category_id", defaultValue = "NULL")
    override val categoryId: Int? = null,
    @ColumnInfo(name = "priority", defaultValue = "0") // 0 = raw-sql without priority specified
    override val priority: Int = 3,
    @ColumnInfo(name = "show_in_monthly_view", defaultValue = "1")
    override val showInMonthlyView: Boolean = true,
    @ColumnInfo(name = "is_overridden", defaultValue = "0")
    override val isOverridden: Boolean = false
) : RawPlan {
    fun toTodo(): Todo = Todo(
        id = id, // Should use same id for successful update
        title = title,
        dueTime = dueTime,
        complete = complete,
        memo = memo,
        repeatGroupId = repeatGroupId,
        categoryId = categoryId,
        priority = priority,
        showInMonthlyView = showInMonthlyView,
        isOverridden = isOverridden
    )
}

fun Todo.toRawTodo(): RawTodo = RawTodo(
    id = id, // Should use same id for successful update
    title = title,
    dueTime = dueTime,
    complete = complete,
    memo = memo,
    repeatGroupId = repeatGroupId,
    categoryId = categoryId,
    priority = priority,
    showInMonthlyView = showInMonthlyView,
    isOverridden = isOverridden
)