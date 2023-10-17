package com.example.calendy.view.editplanview

import com.example.calendy.utils.DateHelper
import java.util.Date

sealed class EntryType {
    object Schedule : EntryType()
    object Todo : EntryType()
}
data class EditPlanUiState(
        val entryType : EntryType = EntryType.Schedule,
        val titleField: String = "",
        val memoField: String = "",
        val categoryID: Int = 0,
        val priority: Int = 1,
        val startTime: Date = Date(),// schedule일 경우 필요
        val endTime: Date = Date() //schedule 과  todo에 모두 필요
)