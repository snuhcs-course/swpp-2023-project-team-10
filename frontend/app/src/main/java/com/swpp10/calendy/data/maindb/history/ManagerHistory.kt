package com.swpp10.calendy.data.maindb.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.swpp10.calendy.data.maindb.message.Message
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.view.messageview.QueryType

@Entity(
    tableName = "manager_history", foreignKeys = [ForeignKey(
        entity = Message::class,
        parentColumns = ["id"],
        childColumns = ["message_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = SavedSchedule::class,
        parentColumns = ["id"],
        childColumns = ["saved_schedule_id"],
        onDelete = ForeignKey.SET_DEFAULT
    ), ForeignKey(
        entity = SavedTodo::class,
        parentColumns = ["id"],
        childColumns = ["saved_todo_id"],
        onDelete = ForeignKey.SET_DEFAULT
    ), ForeignKey(
        entity = Schedule::class,
        parentColumns = ["id"],
        childColumns = ["current_schedule_id"],
        onDelete = ForeignKey.SET_DEFAULT
    ), ForeignKey(
        entity = Todo::class,
        parentColumns = ["id"],
        childColumns = ["current_todo_id"],
        onDelete = ForeignKey.SET_DEFAULT
    )]
)
data class ManagerHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "message_id", index = true)
    val messageId: Int,
    @ColumnInfo(name = "is_schedule")
    val isSchedule: Boolean,
    @ColumnInfo(name = "revision_type")
    val revisionType: QueryType, // insert. update. delete
    @ColumnInfo(name = "saved_schedule_id", index = true)
    val savedScheduleId: Int? = null, // previous plan before revision. may be null
    @ColumnInfo(name = "saved_todo_id", index = true)
    val savedTodoId: Int? = null,
    @ColumnInfo(name = "current_schedule_id", index = true)
    val currentScheduleId: Int? = null,
    @ColumnInfo(name = "current_todo_id", index = true)
    val currentTodoId: Int? = null,
)
