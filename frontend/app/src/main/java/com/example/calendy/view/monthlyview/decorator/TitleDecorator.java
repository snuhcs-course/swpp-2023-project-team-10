package com.example.calendy.view.monthlyview.decorator;

//import com.project.sample_calendar.R;
import com.example.calendy.R;
import com.example.calendy.data.maindb.plan.Plan;
import com.example.calendy.data.maindb.plan.PlanType;
import com.example.calendy.data.maindb.plan.Schedule;
import com.example.calendy.utils.DateHelper;
import com.example.calendy.utils.DateHelperKt;
import com.example.calendy.utils.PlanHelperKt;
import com.example.calendy.view.monthlyview.ILabel;
import com.example.calendy.view.monthlyview.LabelSlot;
import com.example.calendy.view.monthlyview.PlanLabel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

        import java.util.List;
import static com.example.calendy.utils.PlanHelperKt.getPlanType;

/**
 * Decorate several days with a dot
 */
public class TitleDecorator implements DayViewDecorator {

    private CalendarDay targetDay;
    private LabelSlot<Plan> planLabel;
    public TitleDecorator(CalendarDay day, LabelSlot<Plan> planSlot) {
        this.targetDay = day;
        this.planLabel=planSlot;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return targetDay.equals(day);

    }

    @Override
    public void decorate(DayViewFacade view) {
        int count=0;
        int max=5;  //prevent rendering too many plans
        for(ILabel label : planLabel){
            PlanLabel pLabel=(PlanLabel)label;
            Plan plan=pLabel.getItem();
            int index=pLabel.getIndex();
            PlanType type= PlanHelperKt.getPlanType(plan);
            int length=pLabel.getWeight();
            int offset=DateHelperKt.getDiffBetweenDates(pLabel.getStartDate(),DateHelperKt.toDate(targetDay));
            Boolean completed=pLabel.getCompleted();
            SinglePlanSpan span = new SinglePlanSpan(
                    index,
                    plan.getPriority(),
                    plan.getTitle(),
                    type,
                    length,
                    offset,
                    completed
                    );
            view.addSpan(span);

            count++;
            if(count>=max) break;
        }

    }
}