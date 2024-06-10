package com.app.pills.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.app.pills.R
import com.app.pills.databinding.FragmentNewScheduleBinding
import com.app.pills.db.entity.ScheduleCardTimeSlotEntity
import com.app.pills.model.Util
import com.app.pills.view.adapters.OnDeleteListener
import com.app.pills.view.adapters.PillPickAdapter
import com.app.pills.view.adapters.ScheduleCardTimeSlotAdapter
import com.app.pills.viewmodel.NewScheduleViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class NewScheduleFragment : Fragment(R.layout.fragment_new_schedule),  OnDeleteListener {

    private lateinit var binding: FragmentNewScheduleBinding
    private val args: NewScheduleFragmentArgs by navArgs()
    private val viewModel: NewScheduleViewModel by viewModels()

    private lateinit var scheduleCardTimeSlotAdapter: ScheduleCardTimeSlotAdapter
    private lateinit var pillPickAdapter: PillPickAdapter

    private val util = Util()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scheduleId = args.scheduleId

        Log.d("new", scheduleId.toString())

        initState()
        initObservers()
    }

    private fun initState() {
        if (args.scheduleId < 0) {
            viewModel.setStartDate(LocalDateTime.now())
            viewModel.setEndDate(LocalDateTime.now().plusMonths(1))
            viewModel.setWeek(0)
        }
        setListeners()
        setRecycler()
    }

    private fun setListeners() {
        binding.cancelButton.setOnClickListener { findNavController().popBackStack() }
        binding.startDateText.setOnClickListener { startDatePick() }
        binding.endDateText.setOnClickListener { endDatePick() }

        binding.weekMonday.setOnClickListener { changeWeek(0) }
        binding.weekTuesday.setOnClickListener { changeWeek(1) }
        binding.weekWednesday.setOnClickListener { changeWeek(2) }
        binding.weekThursday.setOnClickListener { changeWeek(3) }
        binding.weekFriday.setOnClickListener { changeWeek(4) }
        binding.weekSaturday.setOnClickListener { changeWeek(5) }
        binding.weekSunday.setOnClickListener { changeWeek(6) }

        binding.addTimeSlotButton.setOnClickListener { intakeTimePick() }

        binding.addNewScheduleButton.setOnClickListener {
            viewModel.addNewScheduleCard()
            findNavController().popBackStack()
        }
    }



    private fun startDatePick() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Дата начала приема")
        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val instant = Instant.ofEpochMilli(selection)
            val selectedDate = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            viewModel.setStartDate(selectedDate)
        }

        datePicker.show(requireFragmentManager(), datePicker.toString())
    }

    private fun endDatePick() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Дата конца приема")
        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val instant = Instant.ofEpochMilli(selection)
            val selectedDate = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
            viewModel.setEndDate(selectedDate)
        }

        datePicker.show(requireFragmentManager(), datePicker.toString())
    }

    private fun intakeTimePick() {
        val util = Util()
        val timePicker = MaterialTimePicker.Builder().
        setTimeFormat(TimeFormat.CLOCK_24H).
        setHour(12).
        setMinute(0).
        setTitleText("Время приема").
        build()

        timePicker.show(parentFragmentManager, "MATERIAL_TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val time = util.localTimeToLong(LocalTime.of(hour, minute))
            val newPillIntake = ScheduleCardTimeSlotEntity(time = time, scheduleCardId = -1)
            viewModel.addPillIntake(newPillIntake)
            Log.d("Time Pick", "$hour $minute")
        }

    }

    private fun setRecycler() {
        scheduleCardTimeSlotAdapter = ScheduleCardTimeSlotAdapter(this)
        binding.timeSlotRecycler.adapter = scheduleCardTimeSlotAdapter
        binding.timeSlotRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.timeSlotRecycler)

        viewModel.scheduleCardTimeSlots.observe(viewLifecycleOwner) {intakes ->
            Log.d("Intake observe", "$intakes")
            scheduleCardTimeSlotAdapter.refresh(intakes)
        }

        pillPickAdapter = PillPickAdapter(viewModel)
        binding.pillsRecycler.adapter = pillPickAdapter
        binding.pillsRecycler.layoutManager =LinearLayoutManager(context)
        val pickSnapHelper = LinearSnapHelper()
        pickSnapHelper.attachToRecyclerView(binding.pillsRecycler)

        viewModel.scheduleCardPills.observe(viewLifecycleOwner) { scheduleCardPills ->
            Log.d("scheduleCardPills observe", "$scheduleCardPills")
            pillPickAdapter.refresh(scheduleCardPills)
        }

    }

    private fun initObservers() {

        viewModel.allPills.observe(viewLifecycleOwner) {
            viewModel.setScheduleCardPills()
        }

        viewModel.startDate.observe(viewLifecycleOwner) {
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
            val startDate = util.longToLocalDateTime(it).format(dateFormatter)
            Log.d("OBSERVE", startDate)
            binding.startDateText.text = startDate
        }

        viewModel.endDate.observe(viewLifecycleOwner) {
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
            val endDate = util.longToLocalDateTime(it).format(dateFormatter)
            Log.d("OBSERVE", endDate)
            binding.endDateText.text = endDate
        }

        viewModel.week.observe(viewLifecycleOwner) {
            val newWeek = it ?: 0

            binding.weekMonday.setBackgroundResource(checkWeek(newWeek, 0))
            binding.weekTuesday.setBackgroundResource(checkWeek(newWeek, 1))
            binding.weekWednesday.setBackgroundResource(checkWeek(newWeek, 2))
            binding.weekThursday.setBackgroundResource(checkWeek(newWeek, 3))
            binding.weekFriday.setBackgroundResource(checkWeek(newWeek, 4))
            binding.weekSaturday.setBackgroundResource(checkWeek(newWeek, 5))
            binding.weekSunday.setBackgroundResource(checkWeek(newWeek, 6))
        }


    }

    private fun checkWeek(week: Int, bit: Int): Int {
        val drawableUnSelected = R.drawable.week_circle
        val drawableSelected = R.drawable.week_circle_selected
        return if (((week and (1 shl bit)) shr bit) == 0) drawableUnSelected else drawableSelected
    }

    private fun changeWeek(bit: Int) {
        val week = viewModel.week.value ?: 0
        val newWeek = week xor (1 shl bit)
        viewModel.setWeek(newWeek)
    }

    override fun onDelete(scheduleCardTimeSlotEntity: ScheduleCardTimeSlotEntity) {
        viewModel.removePillIntake(scheduleCardTimeSlotEntity)
    }


}