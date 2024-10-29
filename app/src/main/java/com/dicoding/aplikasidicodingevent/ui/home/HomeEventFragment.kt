package com.dicoding.aplikasidicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.model.EventAdapter
import com.dicoding.aplikasidicodingevent.databinding.FragmentHomeBinding
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.di.Injection
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeEventFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var activeEventAdapter: EventAdapter
    private lateinit var finishedEventAdapter: EventAdapter

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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        mainViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            activeEventAdapter.submitList(events)
        }

        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        mainViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            activeEventAdapter.submitList(searchResults)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        activeEventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event as ListEventsItem)
            }
            startActivity(intent)
        }
        binding.recyclerViewActiveEvents.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActiveEvents.adapter = activeEventAdapter

        finishedEventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event as ListEventsItem)
            }
            startActivity(intent)
        }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFinishedEvents.adapter = finishedEventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
