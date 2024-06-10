package com.app.pills.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.app.pills.R
import com.app.pills.databinding.FragmentScheduleBinding
import com.app.pills.view.adapters.ScheduleAdapter
import com.app.pills.view.adapters.IntakeAdapter
import com.app.pills.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var intakeAdapter: IntakeAdapter
    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
    }

    private fun initState() {
        initObservers()
        openTodaySchedule()
        setTimeSlotAdapter()
        setScheduleAdapter()
        setListeners()
    }

    private fun openTodaySchedule() {
        binding.todayConstraint.visibility = View.VISIBLE
        binding.scheduleConstraint.visibility = View.GONE
    }

    private fun openAllSchedule() {
        binding.todayConstraint.visibility = View.GONE
        binding.scheduleConstraint.visibility = View.VISIBLE
    }

    private fun setListeners() {
        binding.toTodayButton.setOnClickListener { openTodaySchedule() }
        binding.toScheduleButton.setOnClickListener { openAllSchedule() }
        binding.addNewScheduleButton.setOnClickListener { navigateToNewSchedule() }
    }

    private fun setScheduleAdapter() {
        scheduleAdapter = ScheduleAdapter(viewModel)
        binding.scheduleRecycler.adapter = scheduleAdapter
        binding.scheduleRecycler.layoutManager = LinearLayoutManager(context)
        val scheduleSnapHelper = LinearSnapHelper()
        scheduleSnapHelper.attachToRecyclerView(binding.scheduleRecycler)


        viewModel.allScheduleCards.observe(viewLifecycleOwner) {
            Log.d("allScheduleCards OBSERVE", "$it")
            scheduleAdapter.refresh(it)
//            viewModel.todayFilter()
        }

    }

    private fun setTimeSlotAdapter() {
        intakeAdapter = IntakeAdapter(viewModel)
        binding.timeSlotRecycler.adapter = intakeAdapter
        binding.timeSlotRecycler.layoutManager = LinearLayoutManager(context)
        val timeSlotSnapHelper = LinearSnapHelper()
        timeSlotSnapHelper.attachToRecyclerView(binding.timeSlotRecycler)

        viewModel.todayIntakes.observe(viewLifecycleOwner) {
            intakeAdapter.refresh(it)
        }

    }

    private fun initObservers() {

        viewModel.scheduleCards.observe(viewLifecycleOwner) {
            viewModel.updateScheduleCards()
            //viewModel.updateTodayPillIntakes()
            viewModel.createNewIntakes()
        }

        viewModel.allIntakes.observe(viewLifecycleOwner) {
            viewModel.updateTodayPillIntakes()
        }

    }

    private fun navigateToNewSchedule() {
        val action = ScheduleFragmentDirections.actionScheduleFragmentToNewScheduleFragment(-1)
        findNavController().navigate(action)
    }
}