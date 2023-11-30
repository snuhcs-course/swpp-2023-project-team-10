package com.example.calendy.view.monthlyview

import com.example.calendy.data.maindb.plan.Plan
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Hashtable

data class MonthlyPageUIState (
    val selectedDate : CalendarDay = CalendarDay.today(),
    val currentMonth:CalendarDay = CalendarDay.today(),
    val planLabelContainer: PlanLabelContainer = PlanLabelContainer()
)