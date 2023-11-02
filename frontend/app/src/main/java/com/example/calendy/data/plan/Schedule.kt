package com.example.calendy.data.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.calendy.data.category.Category
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.google.gson.annotations.SerializedName
import java.util.Date


@Entity(
    tableName = "schedule", foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.SET_DEFAULT
    ), ForeignKey(
        entity = RepeatGroup::class,
        parentColumns = ["id"],
        childColumns = ["repeat_group_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    override val id: Int = 0,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    override val title: String,
    @ColumnInfo(name = "start_time")
    @SerializedName("start_time")
    val startTime: Date,
    @ColumnInfo(name = "end_time")
    @SerializedName("end_time")
    val endTime: Date,
    @ColumnInfo(name = "memo", defaultValue = "")
    @SerializedName("memo")
    override val memo: String = "",
    @ColumnInfo(name = "repeat_group_id", defaultValue = "NULL")
    @SerializedName("repeat_group_id")
    override val repeatGroupId: Int? = null,
    @ColumnInfo(name = "category_id", defaultValue = "NULL")
    @SerializedName("category_id")
    override val categoryId: Int? = null,
    @ColumnInfo(name = "priority", defaultValue = "3")
    @SerializedName("priority")
    override val priority: Int = 3,
    @ColumnInfo(name = "show_in_monthly_view", defaultValue = "0")
    @SerializedName("show_in_monthly_view")
    override val showInMonthlyView: Boolean = false,
    @ColumnInfo(name = "is_overridden", defaultValue = "0")
    @SerializedName("is_overridden")
    override val isOverridden: Boolean = false
) : Plan
