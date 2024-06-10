package com.app.pills.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.pills.R
import com.app.pills.databinding.PillItemBinding
import com.app.pills.db.entity.PillEntity
import com.app.pills.model.UnitType


class PillAdapter(private val listener: OnEditListener): RecyclerView.Adapter<PillAdapter.PillHolder>() {

    private var pillList: List<PillEntity> = listOf()

    class PillHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = PillItemBinding.bind(item)

        fun bind(pill: PillEntity, listener: OnEditListener) {
            binding.pillName.text = pill.name
            val pillCount = "Осталось ${pill.count} ${UnitType.rule(pill.unitType, pill.count)}"
            binding.pillCount.text = pillCount
            val icon = UnitType.fromText(pill.unitType)?.drawableResId
            if (icon != null) {
                binding.pillImage.setImageResource(icon)
            }
            binding.pillEdit.setOnClickListener { listener.onEdit(pill.id!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pill_item, parent, false)
        return PillHolder(view)
    }

    override fun getItemCount(): Int {
        return pillList.size
    }

    override fun onBindViewHolder(holder: PillHolder, position: Int) {
        val pill = pillList[position]
        holder.bind(pill, listener)
    }

    fun refresh(list: List<PillEntity>?) {
        pillList = list ?: emptyList()
        notifyDataSetChanged()
    }

}