package com.dicoding.aplikasidicodingevent.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.databinding.FragmentUpcomingBinding
import com.dicoding.aplikasidicodingevent.di.Injection
import com.dicoding.aplikasidicodingevent.model.EventAdapter
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.ui.MainActivity
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainViewModel = ViewModelProvider(
            this, MainViewModelFactory(
                Injection.provideRepository(requireContext()),
                Injection.provideThemeDataStore(requireContext()),
                Injection.provideReminderDataStore(requireContext())
            )
        )[MainViewModel::class.java]

        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        mainViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        mainViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            (activity as? MainActivity)?.applyTheme(isDarkModeActive)
        }

    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event as ListEventsItem)
            }
            startActivity(intent)
        }
        binding.recycleApiUpcoming.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleApiUpcoming.adapter = eventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
