package com.example.calendy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.CategoryDao
import com.example.calendy.data.message.Message
import com.example.calendy.data.message.MessageDao
import com.example.calendy.data.schedule.Schedule
import com.example.calendy.data.schedule.ScheduleDao
import com.example.calendy.data.todo.Todo
import com.example.calendy.data.todo.TodoDao
import com.example.calendy.data.user.User

@Database(entities = [Schedule::class, Todo::class, Category::class, Message::class, User::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class CalendyDatabase: RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var Instance: CalendyDatabase? = null
        fun getDatabase(context: Context): CalendyDatabase {
            return Instance ?: synchronized(this) {
                // name: The name of the database file.
                Room.databaseBuilder(context = context, klass = CalendyDatabase::class.java, name = "calendy_database")
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { Instance = it }
            }
        }
    }
}