package com.app.pills.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.app.pills.R
import com.app.pills.databinding.PillPickItemBinding

import com.app.pills.db.entity.PillEntity
import com.app.pills.db.entity.ScheduleCardPillEntity
import com.app.pills.model.UnitType
import com.app.pills.viewmodel.NewScheduleViewModel


class PillPickAdapter(private val viewModel: NewScheduleViewModel): RecyclerView.Adapter<PillPickAdapter.PillPickHolder>() {

    private var pillList: List<ScheduleCardPillEntity> = listOf()

    class PillPickHolder(item: View, private val viewModel: NewScheduleViewModel): RecyclerView.ViewHolder(item) {
        private val binding = PillPickItemBinding.bind(item)

        fun bind(pill: ScheduleCardPillEntity) {
            binding.pillName.text = pill.pillName
            val dosage = pill.dosage
            if (dosage > 0) binding.newPillCountText.setText(dosage.toString()) else binding.newPillCountText.setText("")
            val icon = UnitType.fromText(pill.pillUnitType)?.drawableResId
            if (icon != null) {
                binding.pillImage.setImageResource(icon)
            }

            binding.newPillCountText.setOnEditorActionListener { view, actionId, _ ->

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val newDosage = binding.newPillCountText.text.toString().toIntOrNull() ?: 0
                    viewModel.updateDosage(pill.pillName, newDosage)

                    val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    true
                } else
                    false
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillPickHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pill_pick_item, parent, false)
        return PillPickHolder(view, viewModel)
    }

    override fun getItemCount(): Int {
        return pillList.size
    }

    override fun onBindViewHolder(holder: PillPickHolder, position: Int) {
        val pill = pillList[position]
        holder.bind(pill)
    }

    fun refresh(list: List<ScheduleCardPillEntity>?) {
        pillList = list?.sortedBy { it.pillName } ?: emptyList()
        notifyDataSetChanged()
    }

}