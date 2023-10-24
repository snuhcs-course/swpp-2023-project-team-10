package com.example.calendy.view.monthlyview;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calendy.R;
import com.example.calendy.data.plan.Schedule;
import com.example.calendy.view.monthlyview.decorator.DotDecorator;
import com.example.calendy.view.monthlyview.decorator.OneDayDecorator;
import com.example.calendy.view.monthlyview.decorator.SaturdayDecorator;
import com.example.calendy.view.monthlyview.decorator.SelectedDayDecorator;
import com.example.calendy.view.monthlyview.decorator.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class MonthlyPage extends ViewGroup {
    private final String TAG = this.getClass().getSimpleName();
    private  CalendarDay selectedDate = CalendarDay.today();
    MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private MonthlyViewModel model;
    //temp
    private Hashtable<CalendarDay, List<Schedule>> schedulesOfMonth;
    private SelectedDayDecorator selectedDayDecorator;
    private DotDecorator dotDecorator;
    private ArrayList<Schedule> dummyDaySchedules;

    ActivityResultLauncher<Intent> startActivityResult;

    Context context;

    public MonthlyPage(Context context) {
        super(context);
        this.context = context;
        onCreate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    protected void onCreate() {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_monthly_page, null, true);
        addView(view);
//        model = new ViewModelProvider(this).get(MonthlyViewModel.class);

        selectedDate=CalendarDay.today();
//
        // initial setting for calendar view
        calendarView = findViewById(R.id.materialMonthlyView);
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarView.setSelectedDate(selectedDate);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))   //from 2000.1.1
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) //to 2030.12.31
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // selected day decorator initialization
//        selectedDayDecorator = new SelectedDayDecorator(CalendarDay.today(), context);
        //temp dummy code
        schedulesOfMonth = new Hashtable<>();
        //set dummy
        dummyDaySchedules = new ArrayList<>();
        dummyDaySchedules.add(new Schedule(1231,"test1", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));
        dummyDaySchedules.add(new Schedule(1232,"test2", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));
        dummyDaySchedules.add(new Schedule(1233,"test3", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));
        dummyDaySchedules.add(new Schedule(1234,"test4", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));
        dummyDaySchedules.add(new Schedule(1235,"test5", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));
        dummyDaySchedules.add(new Schedule(1236,"test6", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));
        dummyDaySchedules.add(new Schedule(1237,"test7", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false));

        List<Schedule> daySchedule=dummyDaySchedules;
        schedulesOfMonth.put(CalendarDay.from(2023,9,11),daySchedule);

        dotDecorator = new DotDecorator(schedulesOfMonth);
        calendarView.addDecorators(
                new SundayDecorator()
                ,new SaturdayDecorator()
                ,oneDayDecorator
//                ,selectedDayDecorator
                ,dotDecorator
        );

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            // selected date changed
              @Override
              public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                if(selectedDate.equals(date) && schedulesOfMonth.containsKey(date))
//                    openDayPlanListPopup();
                calendarView.removeDecorator(selectedDayDecorator);
                selectedDate=calendarView.getSelectedDate();
//                selectedDayDecorator = new SelectedDayDecorator(selectedDate, MonthlyPage.this);
                calendarView.addDecorators(selectedDayDecorator);

              }
          });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            // selected month changed
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });

        //for modal view setting
//        startActivityResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//            public void onActivityResult(ActivityResult result) {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    Log.d(TAG, "MainActivity로 돌아왔다. ");
//                }
//            }
//        });
    }

//    private void openDayPlanListPopup() {
//
//        Intent intent = new Intent(MonthlyPage.this, MonthlyDayPlanListPopup.class);
//        intent.putExtra("year",selectedDate.getYear());
//        intent.putExtra("month",selectedDate.getMonth());
//        intent.putExtra("day",selectedDate.getDay());
//        startActivityResult.launch(intent);
//    }


}

