package com.app.pills.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.pills.R
import com.app.pills.databinding.ScheduleItemBinding
import com.app.pills.db.entity.ScheduleCardWithDetails
import com.app.pills.model.UnitType
import com.app.pills.model.Util
import com.app.pills.viewmodel.ScheduleViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleAdapter(private val viewModel: ScheduleViewModel): RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {

    private var scheduleList: List<ScheduleCardWithDetails> = listOf()

    class ScheduleHolder(item: View, private val viewModel: ScheduleViewModel): RecyclerView.ViewHolder(item) {
        private val binding = ScheduleItemBinding.bind(item)
        private val util = Util()

        fun bind(schedule: ScheduleCardWithDetails) {
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
            val startDate = util.longToLocalDateTime(schedule.scheduleCard.firstDate).format(dateFormatter)
            val endDate = util.longToLocalDateTime(schedule.scheduleCard.lastDate).format(dateFormatter)

            binding.startEndDateText.text = "C $startDate по $endDate"

            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            val timeSlotList = schedule.timeSlots

            val timeSlotsLayout = binding.timeSlotsLayout
            timeSlotsLayout.removeAllViews()

            val inflater = LayoutInflater.from(itemView.context)
            for (time in timeSlotList) {
                val textView = inflater.inflate(R.layout.text_view_slot_item, timeSlotsLayout, false) as TextView
                textView.text = util.longToLocalTime(time.time).format(formatter)
                Log.d("Flow:", "add $time")
                timeSlotsLayout.addView(textView)
            }

            val takePillList = schedule.pills
            val textPillItemLinear = binding.textPillItemLinear
            textPillItemLinear.removeAllViews()

            for (takePill in takePillList) {
                val textPillItem = inflater.inflate(R.layout.text_pill_item, textPillItemLinear, false)
                val pillNameTextView = textPillItem.findViewById<TextView>(R.id.pill_name_text)
                val pillCountUnitTextView = textPillItem.findViewById<TextView>(R.id.pill_count_unit_text)
                val icon = textPillItem.findViewById<ImageView>(R.id.pill_intake_image_view)
                icon.setImageResource(UnitType.fromText(takePill.pillUnitType)!!.drawableResId)

                pillNameTextView.text = takePill.pillName

                pillCountUnitTextView.text = "${takePill.dosage} ${UnitType.rule(takePill.pillUnitType, takePill.dosage)}"
                textPillItemLinear.addView(textPillItem)

            }

            binding.weekMonday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 0))
            binding.weekTuesday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 1))
            binding.weekWednesday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 2))
            binding.weekThursday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 3))
            binding.weekFriday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 4))
            binding.weekSaturday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 5))
            binding.weekSunday.setBackgroundResource(checkWeek(schedule.scheduleCard.daysOfWeek, 6))

            val count = takePillList.size
            val rule = UnitType.rule("", count)
            binding.pillCount.text = "Принять $count $rule"

            binding.scheduleDeleteButton.setOnClickListener {
                viewModel.deleteSchedule(schedule.scheduleCard)
            }

        }

        private fun checkWeek(week: Int, bit: Int): Int {
            val drawableUnSelected = R.drawable.week_circle
            val drawableSelected = R.drawable.week_circle_selected
            return if (((week and (1 shl bit)) shr bit) == 0) drawableUnSelected else drawableSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ScheduleHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        val schedule = scheduleList[position]
        holder.bind(schedule)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    fun refresh(list: List<ScheduleCardWithDetails>?) {
        scheduleList = list ?: emptyList()
        notifyDataSetChanged()
    }

}