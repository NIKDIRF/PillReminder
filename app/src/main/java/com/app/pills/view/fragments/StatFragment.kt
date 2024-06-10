package com.app.pills.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.pills.R
import com.app.pills.databinding.FragmentStatBinding
import com.app.pills.viewmodel.StatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatFragment : Fragment(R.layout.fragment_stat) {

    private lateinit var binding: FragmentStatBinding
    private val viewModel: StatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initState()
        initObservers()
    }

    private fun initState() {
        binding.progressBar.progress = 90
        binding.progressDayBar.progress = 10
        binding.progressWeekBar.progress = 20
        binding.progressMonthBar.progress = 30
    }

    private fun initObservers() {

        viewModel.allIntakes.observe(viewLifecycleOwner) {
            viewModel.updateTodayPillIntakes()
        }

        viewModel.todayIntakes.observe(viewLifecycleOwner) {
            val allCount = if (it.isEmpty()) 1 else it.size
            val doneCount = it.count { intake -> intake.pillIntake.isDone == 1}

            val hp = 50 + doneCount * 10
            Log.d("COUNT/DONE", "$allCount/$doneCount")
            binding.progressDayBar.progress = ((allCount - doneCount) * 100 / allCount)
            binding.progressBar.progress = hp

            when (hp) {
                0 -> binding.snake.setImageResource(R.drawable.snake_5)
                in 1..25 -> binding.snake.setImageResource(R.drawable.snake_4)
                in 26..50 -> binding.snake.setImageResource(R.drawable.snake_3)
                in 51..75 -> binding.snake.setImageResource(R.drawable.snake_2)
                in 76..100 -> binding.snake.setImageResource(R.drawable.snake_1)
            }
        }
    }

}