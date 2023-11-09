package com.example.calendy.data.plan.schedule

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IScheduleRepository {
    suspend fun insertSchedule(schedule: Schedule)
    suspend fun deleteSchedule(schedule: Schedule)
    suspend fun updateSchedule(schedule: Schedule)
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
    fun getScheduleById(id: Int): Flow<Schedule>
    fun getAllSchedule(): Flow<List<Schedule>>
    fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>
    suspend fun getSchedulesByIds(ids: List<Int>): List<Schedule>
}