package com.dicoding.aplikasidicodingevent.ui.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.aplikasidicodingevent.databinding.FragmentSettingBinding
import com.dicoding.aplikasidicodingevent.di.Injection
import com.dicoding.aplikasidicodingevent.ui.MainActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModelFactory
import com.dicoding.aplikasidicodingevent.worker.ReminderWorker
import java.util.concurrent.TimeUnit

class SettingEventFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startReminderWorker()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                Injection.provideRepository(requireContext()),
                Injection.provideThemeDataStore(requireContext()),
                Injection.provideReminderDataStore(requireContext())
            )
        )[MainViewModel::class.java]

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            (activity as? MainActivity)?.applyTheme(isDarkModeActive)
            binding.switchTheme.isChecked = isDarkModeActive
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        mainViewModel.getReminderSetting().observe(viewLifecycleOwner) { isReminderActive ->
            binding.switchReminder.isChecked = isReminderActive
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            mainViewModel.saveReminderSetting(isChecked)
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= 33) {
                    requestNotificationPermission()
                } else {
                    startReminderWorker()
                }
            } else {
                cancelReminderWorker()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startReminderWorker()
        }
    }

    private fun startReminderWorker() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelReminderWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("reminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}