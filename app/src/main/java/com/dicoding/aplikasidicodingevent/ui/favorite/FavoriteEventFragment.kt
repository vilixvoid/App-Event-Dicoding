package com.dicoding.aplikasidicodingevent.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.di.Injection
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import com.dicoding.aplikasidicodingevent.databinding.FragmentFavoriteBinding
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.dicoding.aplikasidicodingevent.model.EventAdapter
import com.dicoding.aplikasidicodingevent.ui.MainActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModelFactory

class FavoriteEventFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
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

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        showLoading(true)

        mainViewModel.getFavoriteEvents().observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            showLoading(false)
            if (events.isEmpty()) {
                binding.tvNoFavoriteEvents.visibility = View.VISIBLE
            } else {
                binding.tvNoFavoriteEvents.visibility = View.GONE
            }
        }

        mainViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            (activity as? MainActivity)?.applyTheme(isDarkModeActive)
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            when (event) {
                is ListEventsItem -> intent.putExtra("event", event)
                is EventEntity -> intent.putExtra("event", event.toListEventsItem())
            }
            startActivity(intent)
        }
        binding.rvFavoriteEvents.layoutManager = LinearLayoutManager(context)
        binding.rvFavoriteEvents.adapter = eventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
