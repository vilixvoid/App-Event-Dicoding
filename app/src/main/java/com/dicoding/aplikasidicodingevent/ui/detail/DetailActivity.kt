package com.dicoding.aplikasidicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import com.dicoding.aplikasidicodingevent.databinding.ActivityDetailBinding
import com.dicoding.aplikasidicodingevent.di.Injection
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: MainViewModel
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = Injection.provideRepository(this)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                repository,
                Injection.provideThemeDataStore(this),
                Injection.provideReminderDataStore(this)
            )
        )[MainViewModel::class.java]

        viewModel.getThemeSetting().observe(this) { isDarkModeActive ->
            applyTheme(isDarkModeActive)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        val event = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("event", ListEventsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
        }

        event?.let {
            viewModel.isEventFavorite(event.id!!).observe(this) { isFav ->
                isFavorite = isFav
                setFavoriteIcon()
            }

            with(binding) {
                tvDetailName.text = event.name
                tvDetailOwnername.text = event.ownerName
                tvDetailBegintime.text = event.beginTime
                tvDetailQuota.text = getString(R.string.quota_left, event.quota?.minus(event.registrants ?: 0))
                tvDetailDescription.text = event.description?.let {
                    HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                } ?: ""
            }

            val imageView: ImageView = binding.ivImageUpcoming
            Glide.with(this).load(event.imageLogo ?: event.mediaCover).into(imageView)

            binding.btnDetailSign.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(event.link)
                startActivity(intent)
            }

            binding.fabFavorite.setOnClickListener {
                isFavorite = !isFavorite
                setFavoriteIcon()
                if (isFavorite) {
                    viewModel.insertEvent(
                        EventEntity(
                            event.id,
                            event.name,
                            event.ownerName,
                            event.beginTime,
                            event.imageLogo,
                            event.mediaCover,
                            event.registrants,
                            event.link,
                            event.description,
                            event.quota
                        )
                    )
                    Toast.makeText(this, "Event ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.deleteEvent(
                        EventEntity(
                            event.id,
                            event.name,
                            event.ownerName,
                            event.beginTime,
                            event.imageLogo,
                            event.mediaCover,
                            event.registrants,
                            event.link,
                            event.description,
                            event.quota
                        )
                    )
                    Toast.makeText(this, "Event dihapus dari favorit", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Snackbar.make(binding.root, "Event tidak ditemukan", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setFavoriteIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_red))
        } else {
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_red))
        }
    }

    private fun applyTheme(isDarkModeActive: Boolean) {
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
