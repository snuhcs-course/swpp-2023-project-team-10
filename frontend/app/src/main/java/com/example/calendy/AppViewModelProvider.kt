package com.example.calendy

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.view.messagepage.MessagePageViewModel
import com.example.calendy.data.emptydb.EmptyDatabase
import com.example.calendy.view.editplanview.EditPlanViewModel
import com.example.calendy.view.messagepage.MessagePlanLogViewModel
import com.example.calendy.view.monthlyview.MonthlyViewModel
import com.example.calendy.view.todolistview.TodoListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            EditPlanViewModel(
                calendyApplication().container.scheduleRepository,
                calendyApplication().container.todoRepository,
                calendyApplication().container.categoryRepository,
                calendyApplication().container.repeatGroupRepository
            )
        }
        initializer {
            MonthlyViewModel(
                calendyApplication().container.planRepository,
            )
        }
        initializer {
            MessagePageViewModel(
                calendyApplication().container.planRepository,
                calendyApplication().container.messageRepository,
                calendyApplication().container.categoryRepository,
                emptyDatabase = EmptyDatabase.getDatabase(calendyApplication().applicationContext),
                logPlanRepository = calendyApplication().container.logPlanRepository,
            )
        }
        initializer {
            TodoListViewModel(
                calendyApplication().container.todoRepository
            )
        }
        initializer {
            MessagePlanLogViewModel(
                calendyApplication().container.messageRepository,
                calendyApplication().container.planRepository,
                calendyApplication().container.logPlanRepository
            )

        }
    }

}

fun CreationExtras.calendyApplication(): CalendyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CalendyApplication)