package com.example.calendy.view.monthlyview

import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.utils.afterDays
import com.example.calendy.utils.dateOnly
import com.example.calendy.utils.equalDay
import com.example.calendy.utils.getDiffBetweenDates
import com.example.calendy.utils.getPlanType
import java.util.Date
import java.util.Hashtable

/*
 * This class is used to represent a slot in the monthly view that contains a plan label.
 * It is needed to display the plan label in the correct position in the monthly view.
 */
class PlanLabelContainer : Iterable<Pair<Date, LabelSlot<Plan>>> {
    private val slots = Hashtable<Date, LabelSlot<Plan>>() // date, slot. date should be dateOnly

    fun setPlans(plans: List<Plan>): PlanLabelContainer {
        // convert plans to plan labels

        // clear slots, preventing duplicate plans
        slots.clear()

        val planLabels = mutableListOf<PlanLabel>()
        for (plan in plans) {
            planLabels.add(PlanLabel(plan, plan))
        }

        // sort plan labels
        // higher priority, heavier weight first
        planLabels.sortDescending()

        // add plan labels to slots
        // find smallest index that does not have a plan label, for all date of plan period
        for (planLabel in planLabels) {
            val plan = planLabel.item
            val planType = plan.getPlanType()
            val startTime = if (planType == PlanType.SCHEDULE) {
                (plan as Schedule).startTime
            } else {
                (plan as Todo).dueTime
            }
            val endTime = if (planType == PlanType.SCHEDULE) {
                (plan as Schedule).endTime
            } else {
                (plan as Todo).dueTime
            }

            // get date list between start and end date
            val dateList = mutableListOf<Date>()
            var currentDate = startTime.dateOnly()
            val endDate = endTime.afterDays(1).dateOnly()
            do {
                // add label to slot
                slots.putIfAbsent(currentDate, LabelSlot())
                // add date to date list
                dateList.add(currentDate)
                currentDate = currentDate.afterDays(1)
            } while (!currentDate.equalDay(endDate))

            // find smallest index that does not have a plan label
            var index = -1
            var found = false
            while (!found) {
                index++
                found = true
                for (date in dateList) {
                    // if slot at index is not empty, increment index
                    if (slots.get(date)!!.hasItemAt(index)) {
                        found = false
                        break
                    }
                }
            }

            // index is the smallest index that does not have a plan label
            // add plan label to slots
            for (date in dateList) {
                slots.get(date)!!.setItemAt(index, planLabel)
            }
        }

        return this
    }

    fun hasPlanAt(date: Date): Boolean = slots.containsKey(date.dateOnly())

    fun getPlansAt(date: Date): List<Plan> {
        if (!slots.containsKey(date.dateOnly())) return emptyList() else return slots.get(date.dateOnly())!!.getAllItems()
    }

    fun getSlotAt(date: Date): LabelSlot<Plan>? = if (!slots.containsKey(date.dateOnly())) null else slots.get(date.dateOnly())!!

    override fun iterator(): Iterator<Pair<Date, LabelSlot<Plan>>> = slots.entries.iterator().asSequence()
        .map { (date, labelSlot) -> Pair(date, labelSlot) }
        .iterator()
}



class LabelSlot<T>: Iterable<ILabel<T>>{
    val labels = Hashtable<Int,ILabel<T>>()  // index, label

    fun hasItemAt(index:Int):Boolean = labels.containsKey(index)
    fun setItemAt(index:Int, label:ILabel<T>){
        if(hasItemAt(index))
            throw Exception("LabelSlot: setItemAt: index already has an item")
//        else
        label.index= index
        labels.put(index,label)
    }
    fun getItemAt(index:Int):ILabel<T>? = labels.get(index)
    fun getAllItems():List<T> = labels.values.map { it.item }
    fun count():Int = labels.size
    override fun iterator(): Iterator<ILabel<T>> = labels.values.iterator()
}


interface ILabel<T>: Comparable<ILabel<T>>{
    val item:T
    var weight:Int
    var index:Int
}
class PlanLabel(
    override val item: Plan,
    val plan: Plan,
): ILabel<Plan>
{
    override var weight: Int = 0 // weight of the plan label, which is length of the plan in days
    override var index: Int = 0

    private val startDate: Date
    private val endDate: Date

    fun getPlanType(): PlanType = plan!!.getPlanType()
    fun getPriority(): Int = plan!!.priority
    fun getStartDate(): Date = startDate
    fun getEndDate(): Date = endDate
    fun getCompleted(): Boolean = if(getPlanType() == PlanType.SCHEDULE) false else (plan as Todo).complete

    init{
        this.startDate = if(getPlanType() == PlanType.SCHEDULE) (plan as Schedule).startTime.dateOnly() else (plan as Todo).dueTime.dateOnly()
        this.endDate = if(getPlanType() == PlanType.SCHEDULE) (plan as Schedule).endTime.dateOnly() else (plan as Todo).dueTime.dateOnly()
        this.weight = getDiffBetweenDates(startDate,endDate)+1
    }


    override fun compareTo(otherLabel: ILabel<Plan>): Int {
        // greater than 0 if this is greater than other
        // priority first, then weight, then start time or due time, then plan type (schedule first), then end time if schedule, then title
        // earlier first for time comparison
        val other = otherLabel as PlanLabel

        //priority
        if(this.getPriority() != other.getPriority()) return this.getPriority() - other.getPriority()

        //weight
        if (this.weight != other.weight) return this.weight - other.weight


        //start time or due time
        val thisStartTime = if(this.getPlanType() == PlanType.SCHEDULE) (this.plan as Schedule).startTime else (this.plan as Todo).dueTime
        val otherStartTime = if(other.getPlanType() == PlanType.SCHEDULE) (other.plan as Schedule).startTime else (other.plan as Todo).dueTime
        if(thisStartTime != otherStartTime) return otherStartTime.compareTo(thisStartTime)

        //plan type
        if(this.getPlanType() != other.getPlanType()) return other.getPlanType().compareTo(this.getPlanType())

        //end time if schedule
        if(this.getPlanType() == PlanType.SCHEDULE){
            val thisEndTime = (this.plan as Schedule).endTime
            val otherEndTime = (other.plan as Schedule).endTime
            if(thisEndTime != otherEndTime) return otherEndTime.compareTo(thisEndTime)
        }

        //title
        return this.plan.title.compareTo(other.plan.title)
    }


}