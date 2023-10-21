package com.example.calendy

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.calendy.data.plan.schedule.ScheduleDao
import org.junit.Before
import org.junit.runner.RunWith
import androidx.room.Room
import com.example.calendy.data.CalendyDatabase
import org.junit.After
import java.io.IOException
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.schedule.ScheduleLocalDataSource
import com.example.calendy.data.plan.schedule.ScheduleRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ScheduleDaoTest {
    private lateinit var scheduleDao: ScheduleDao
    private lateinit var scheduleDatabase: CalendyDatabase
    private lateinit var scheduleLocalDataSource: ScheduleLocalDataSource
    private lateinit var scheduleRepository: ScheduleRepository
    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        scheduleDatabase = Room.inMemoryDatabaseBuilder(context, CalendyDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        scheduleDao = scheduleDatabase.scheduleDao()
        scheduleLocalDataSource = ScheduleLocalDataSource(scheduleDao)
        scheduleRepository = ScheduleRepository(scheduleLocalDataSource)
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        scheduleDatabase.close()
    }
    fun makeDate(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, millisecond: Int = 0): Date = with(Calendar.getInstance()) {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DATE, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
        set(Calendar.MILLISECOND, millisecond)
        time
    }
    private var schedule1 = Schedule(
            id = 1,
            title = "first",
            startTime = makeDate(2023, 10, 9),
            endTime = makeDate(2023, 10, 11),
            memo = "",
            priority = 1,
            showInMonthlyView = false,
            isOverridden = false
    )
    private var schedule2 = Schedule(
            id = 2,
            title = "second",
            startTime = makeDate(2023, 10, 13, 12, 30),
            endTime = makeDate(2023,11,1),
            memo = "",
            priority = 2,
            showInMonthlyView = false,
            isOverridden = false
    )

    private suspend fun addTwoItemToDb() {
        scheduleRepository.insertSchedule(schedule1)
        scheduleRepository.insertSchedule(schedule2)
    }


    @Test
    @Throws(Exception::class)
    fun daoInsert_test() = runBlocking {
        addTwoItemToDb()
        var allSchedules = scheduleRepository.getSchedulesStream(makeDate(2023,10,9,12),makeDate(2023, 10,10)).first()
        assertEquals(allSchedules[0], schedule1)
        var scheduleId1 = scheduleRepository.getScheduleById(1)
        assertEquals(scheduleId1.first(), schedule1)
    }
}