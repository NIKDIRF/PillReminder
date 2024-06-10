package com.app.pills.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.pills.R
import com.app.pills.databinding.TimeSlotItemBinding
import com.app.pills.db.entity.PillIntakeWithDetails
import com.app.pills.model.UnitType
import com.app.pills.model.Util
import com.app.pills.viewmodel.ScheduleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class IntakeAdapter(private val viewModel: ScheduleViewModel): RecyclerView.Adapter<IntakeAdapter.TimeSlotHolder>() {

    private var timeSlotList: List<PillIntakeWithDetails> = mutableListOf()

    class TimeSlotHolder(item: View, private val viewModel: ScheduleViewModel): RecyclerView.ViewHolder(item) {
        private val binding = TimeSlotItemBinding.bind(item)
        private val util = Util()

        fun bind(intake: PillIntakeWithDetails) {

            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val newTime = util.longToLocalDateTime(intake.pillIntake.time).format(formatter)
            binding.timeText.text = newTime

            Log.d("Intake Recycler item Time:", "$newTime")

            val takePillList = intake.pills
            val textPillItemLinear = binding.textPillItemLinear
            textPillItemLinear.removeAllViews()

            if (intake.pillIntake.isDone == 1) {
                binding.intakePillLinear.setBackgroundResource(R.color.green_200)
            } else {
                binding.intakePillLinear.setBackgroundResource(R.color.blue_100)

            }

            val inflater = LayoutInflater.from(itemView.context)
            for (takePill in takePillList) {
                val textPillItem = inflater.inflate(R.layout.text_pill_item, textPillItemLinear, false)
                val pillNameTextView = textPillItem.findViewById(R.id.pill_name_text) as TextView
                val pillCountUnitTextView = textPillItem.findViewById(R.id.pill_count_unit_text) as TextView
                val pillImage = textPillItem.findViewById(R.id.pill_intake_image_view) as ImageView
                UnitType.fromText(takePill.pillUnitType)
                    ?.let { pillImage.setImageResource(it.drawableResId) }
                pillNameTextView.text = takePill.pillName
                Log.d("Intake Recycler item name ", "${takePill.pillName}")
                pillCountUnitTextView.text = "${takePill.dosage} ${UnitType.rule(takePill.pillUnitType, takePill.dosage)}"
                textPillItemLinear.addView(textPillItem)
            }

            val count = takePillList.size
            val rule = UnitType.rule("", count)
            binding.pillCount.text = "Принять $count $rule"

            binding.textPillItemLinear.visibility = View.GONE

            binding.timeSlotPillCard.setOnClickListener {
                when (binding.textPillItemLinear.visibility) {
                    View.GONE -> binding.textPillItemLinear.visibility = View.VISIBLE
                    View.VISIBLE -> binding.textPillItemLinear.visibility = View.GONE
                }
            }

            binding.scheduleCompleteButton.setOnClickListener { view ->
                var succes = true
                CoroutineScope(Dispatchers.IO).launch {
//                    intake.pills.forEach {
//
//                        succes = (succes and (viewModel.getPillCountByName(name = it.pillName) > it.dosage))
//                        Log.d("COUNT CHECK", "$succes ${it.dosage}")
//                    }

                    if (succes) intake.pills.forEach {  viewModel.updatePillByName(it.pillName, it.dosage)}
                }
                if (succes && intake.pillIntake.isDone == 0) {
                    viewModel.updateIntake(intake.pillIntake.copy(isDone = 1))
                    binding.intakePillLinear.setBackgroundResource(R.color.green_200)
                } else {
                    binding.intakePillLinear.setBackgroundResource(R.color.red_200)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.time_slot_item, parent, false)
        return TimeSlotHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: TimeSlotHolder, position: Int) {
        val timeSlot = timeSlotList[position]
        holder.bind(timeSlot)
    }

    override fun getItemCount(): Int {
        return timeSlotList.size
    }

    fun refresh(list: List<PillIntakeWithDetails>) {
        timeSlotList = list.sortedBy { it.pillIntake.time } ?: listOf()


        notifyDataSetChanged()
    }

}