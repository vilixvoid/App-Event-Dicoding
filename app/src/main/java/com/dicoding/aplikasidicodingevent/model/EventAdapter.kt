package com.dicoding.aplikasidicodingevent.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import com.dicoding.aplikasidicodingevent.databinding.ItemEventBinding

class EventAdapter(
    private val itemClickListener: (Any) -> Unit
) : ListAdapter<Any, EventAdapter.EventViewHolder>(DiffCallback) {

    class EventViewHolder(private var binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Any) {
            with(binding) {
                when (event) {
                    is ListEventsItem -> {
                        tvItemName.text = event.name
                        tvItemOwner.text = event.ownerName
                        tvItemTime.text = event.beginTime

                        Glide.with(itemView)
                            .load(event.imageLogo ?: event.mediaCover)
                            .placeholder(R.drawable.ic_placeholder_image)
                            .error(R.drawable.ic_error_image)
                            .into(ivItemImage)
                    }

                    is EventEntity -> {
                        tvItemName.text = event.name
                        tvItemOwner.text = event.ownerName
                        tvItemTime.text = event.beginTime

                        Glide.with(itemView)
                            .load(event.imageLogo ?: event.mediaCover)
                            .placeholder(R.drawable.ic_placeholder_image)
                            .error(R.drawable.ic_error_image)
                            .into(ivItemImage)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener(getItem(position))
            }
        }
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.apply {
            bind(getItem(position))
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean
            {
                return when {
                    oldItem is ListEventsItem && newItem is ListEventsItem -> oldItem.id == newItem.id
                    oldItem is EventEntity && newItem is EventEntity -> oldItem.id == newItem.id
                    else -> oldItem == newItem
                }
            }
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }
        }
    }
}
