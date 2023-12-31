package com.swpp10.calendy.data.maindb.plan.todo

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.swpp10.calendy.data.BaseDao
import com.swpp10.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TodoDao : BaseDao<Todo> {
    @Query("SELECT * FROM todo")
    fun getAllTodosStream(): Flow<List<Todo>>

    // startTime, endTime inclusive
    @Query("SELECT * FROM todo WHERE due_time BETWEEN :startTime AND :endTime")
    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>

    @Query("SELECT * FROM todo WHERE id = :id")
    fun getTodoById(id: Int): Todo

    @RawQuery
    fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>

    @Query("SELECT * FROM todo WHERE id IN (:iDs)")
    fun getTodosByIds(iDs: List<Int>): List<Todo>

    @Query("SELECT * FROM todo WHERE show_in_monthly_view > 0 AND due_time BETWEEN :startTime AND :endTime")
    fun getMonthlyTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>
}