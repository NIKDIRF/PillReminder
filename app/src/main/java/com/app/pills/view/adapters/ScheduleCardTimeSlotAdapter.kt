package com.app.pills.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.pills.R
import com.app.pills.databinding.LinearSlotItemBinding
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity
import com.app.pills.model.Util
import java.time.format.DateTimeFormatter

class ScheduleCardTimeSlotAdapter(private val listener: OnDeleteListener): RecyclerView.Adapter<ScheduleCardTimeSlotAdapter.PillIntakeHolder>() {

    private var pillIntakeList: MutableList<ScheduleCardTimeSlotEntity> = mutableListOf()

    class PillIntakeHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = LinearSlotItemBinding.bind(item)
        private val util = Util()

        fun bind(pillIntake: ScheduleCardTimeSlotEntity, listener: OnDeleteListener) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = util.longToLocalTime(pillIntake.time).format(formatter)
            binding.textViewItem.text = time
            binding.cancelButton.setOnClickListener { listener.onDelete(pillIntake) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillIntakeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.linear_slot_item, parent, false)
        return PillIntakeHolder(view)
    }

    override fun onBindViewHolder(holder: PillIntakeHolder, position: Int) {
        val pillIntake = pillIntakeList[position]
        holder.bind(pillIntake, listener)
    }

    override fun getItemCount(): Int {
        return pillIntakeList.size
    }

    fun refresh(list: MutableList<ScheduleCardTimeSlotEntity>?) {
        pillIntakeList = list ?: mutableListOf()
        notifyDataSetChanged()
    }


}