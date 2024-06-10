package com.app.pills.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.app.pills.R
import com.app.pills.databinding.FragmentPillsBinding
import com.app.pills.db.entity.PillEntity
import com.app.pills.model.UnitType
import com.app.pills.view.adapters.OnEditListener
import com.app.pills.view.adapters.PillAdapter
import com.app.pills.viewmodel.PillViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PillsFragment : Fragment(R.layout.fragment_pills), OnEditListener {

    private lateinit var binding: FragmentPillsBinding
    private lateinit var adapter: PillAdapter
    private val viewModel: PillViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initState()
    }

    private fun initState() {
        binding.pillOptionsCard.visibility = View.GONE
        binding.pillAddConstraint.visibility = View.GONE
        binding.pillEditConstraint.visibility = View.GONE

        binding.newPillSaveButton.visibility = View.GONE
        binding.editPillButton.visibility = View.GONE
        binding.cancelButton.visibility = View.GONE

        setRecycler()
        setListeners()
    }

    private fun cancelNewPillForm() {
        binding.openAddFormButton.visibility = View.VISIBLE
        binding.pillAddConstraint.visibility = View.GONE
        binding.cancelButton.visibility = View.GONE
        binding.pillOptionsCard.visibility = View.GONE
        binding.newPillSaveButton.visibility = View.GONE
        binding.editPillButton.visibility = View.GONE
        binding.pillEditConstraint.visibility = View.GONE
        binding.pillDeleteButton.visibility = View.GONE
    }

    private fun openNewPillForm() {
        binding.pillAddCardTitle.text = "Новое лекарство"
        binding.openAddFormButton.visibility = View.GONE

        binding.newPillUnit.visibility = View.VISIBLE
        binding.pillOptionsCard.visibility = View.VISIBLE
        binding.pillAddConstraint.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.VISIBLE
        binding.newPillSaveButton.visibility = View.VISIBLE
        binding.newPillCount.visibility = View.VISIBLE
        cleanForm()
        binding.newPillSaveButton.visibility = View.VISIBLE
    }

    private fun openEditPillForm(pillId: Int) {
        binding.pillAddCardTitle.text = "Редактировать лекарство"
        binding.openAddFormButton.visibility = View.GONE

        binding.pillOptionsCard.visibility = View.VISIBLE
        binding.pillAddConstraint.visibility = View.VISIBLE
        binding.newPillCount.visibility = View.GONE
        binding.pillEditConstraint.visibility = View.VISIBLE

        binding.pillEditConstraint.visibility = View.VISIBLE
        binding.editPillButton.visibility = View.VISIBLE
        binding.cancelButton.visibility = View.VISIBLE
        binding.pillDeleteButton.visibility = View.VISIBLE

        setPillDataById(pillId)

    }

    private fun setPillDataById(pillId: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            val editPill = viewModel.getPillById(pillId)

            CoroutineScope(Dispatchers.Main).launch {
                binding.newPillNameText.setText(editPill.name)
                val unitArray = resources.getStringArray(R.array.pill_unit_array)
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    unitArray
                )
                binding.newPillUnitSelect.setText(editPill.unitType, false)
                binding.editPillCount.setText(editPill.count.toString())

                val icon = UnitType.fromText(editPill.unitType)?.drawableResId
                if (icon != null) {
                    binding.newPillUnitImage.setImageResource(icon)
                }


                binding.editPillButton.setOnClickListener {
                    viewModel.updatePill(
                        PillEntity(
                            editPill.id,
                            binding.newPillNameText.text.toString(),
                            binding.newPillUnitSelect.text.toString(),
                            binding.editPillCount.text.toString().toInt()
                        )
                    )
                    cancelNewPillForm()
                }

                binding.pillDeleteButton.setOnClickListener {
                    viewModel.deletePill(editPill)
                    cancelNewPillForm()
                }
            }
        }

    }

    private fun setRecycler() {
        adapter = PillAdapter(this)
        binding.pillsRecycler.adapter = adapter
        binding.pillsRecycler.layoutManager = LinearLayoutManager(context)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.pillsRecycler)

        viewModel.allPills.observe(viewLifecycleOwner) { pills ->

            adapter.refresh(pills)
        }
    }

    private fun setListeners() {
        binding.openAddFormButton.setOnClickListener { openNewPillForm() }
        binding.cancelButton.setOnClickListener { cancelNewPillForm() }

        binding.newPillUnitSelect.setOnItemClickListener { _, view, position, l ->
            val unitTypeText = UnitType.list[position]
            val icon = UnitType.fromText(unitTypeText)?.drawableResId
            if (icon != null) {
                binding.newPillUnitImage.setImageResource(icon)
            }
        }

        binding.newPillSaveButton.setOnClickListener { addNewPill() }
        binding.minusButton.setOnClickListener { onMinusClick() }
        binding.plusButton.setOnClickListener { onPlusClick() }
    }

    private fun cleanForm() {
        binding.newPillNameText.setText("")
        binding.newPillUnitSelect.setText("")
        binding.newPillCountText.setText("")
        binding.newPillUnitImage.setImageDrawable(null)
    }

    private fun addNewPill() {
        val newPillName = binding.newPillNameText.text.toString()
        val newPillCount = binding.newPillCountText.text.toString()
        val newPillUnitText = binding.newPillUnitSelect.text.toString()
        if (newPillName == "") binding.newPillName.error = "Введите название"
        if (newPillCount == "") binding.newPillCount.error = "Введите количество"
        if (newPillUnitText == "") binding.newPillUnit.error = "Выберите единицу измерения"

        if (newPillName != "" && newPillCount != "" && newPillUnitText != "") {

            val newPill = PillEntity(0, newPillName, newPillUnitText, newPillCount.toInt())
            viewModel.insertPill(
                PillEntity(
                    name = newPillName,
                    unitType = newPillUnitText,
                    count = newPillCount.toInt()
                )
            )

            cancelNewPillForm()
        }
    }


    fun onMinusClick() {
        var count = binding.editPillCount.text.toString().toInt()
        if (count >= 1) count--
        binding.editPillCount.setText(count.toString())
    }

    fun onPlusClick() {
        var count = binding.editPillCount.text.toString().toInt()
        if (count < 999) count++
        binding.editPillCount.setText(count.toString())
    }

    override fun onEdit(pillId: Int) {
        openEditPillForm(pillId)
    }

    private fun showShortToast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

}