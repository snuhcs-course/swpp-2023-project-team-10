package com.example.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DummyTodoRepository: ITodoRepository {
    override suspend fun insertTodo(todo: Todo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTodo(todo: Todo) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTodo(todo: Todo) {
        TODO("Not yet implemented")
    }

    override fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getYearlyTodosStream(year: Int): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getMonthlyTodosStream(year: Int, month: Int): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getDailyTodosStream(year: Int, month: Int, day: Int): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getTodoById(id: Int): Flow<Todo> {
        TODO("Not yet implemented")
    }

    override fun getAllTodo(): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> {
        TODO("Not yet implemented")
    }
}