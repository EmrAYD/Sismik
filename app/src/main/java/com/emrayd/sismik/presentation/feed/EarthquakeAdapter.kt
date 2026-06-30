package com.emrayd.sismik.presentation.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emrayd.sismik.databinding.ItemEarthquakeBinding
import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.util.magnitudeToColor
import com.emrayd.sismik.util.toReadableDate
class EarthquakeAdapter(
    private val onItemClick: (Earthquake) -> Unit
) : ListAdapter<Earthquake, EarthquakeAdapter.EarthquakeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        val binding = ItemEarthquakeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EarthquakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EarthquakeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EarthquakeViewHolder(
        private val binding: ItemEarthquakeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(earthquake: Earthquake) {
            binding.apply {
                textMagnitude.text = String.format("%.1f", earthquake.magnitude)
                magnitudeBadge.setCardBackgroundColor(magnitudeToColor(earthquake.magnitude))
                textTitle.text = earthquake.title
                textDepth.text = root.context.getString(
                    com.emrayd.sismik.R.string.format_depth, earthquake.depth
                )
                textDateTime.text = earthquake.epochSeconds.toReadableDate()

                root.setOnClickListener { onItemClick(earthquake) }
                // Etkilenen iller: merkez il + yakın iller (merkez zaten listede yoksa ekle)
                val affectedCities = buildList {
                    if (earthquake.epicenterCity.isNotBlank()) {
                        add(earthquake.epicenterCity)
                    }
                    earthquake.closestCities
                        .filter { !it.equals(earthquake.epicenterCity, ignoreCase = true) }
                        .take(3)  // merkez + 3 yakın il, toplam max 4 il göster
                        .forEach { add(it) }
                }

                textClosestCity.text = root.context.getString(
                    com.emrayd.sismik.R.string.format_affected_cities,
                    earthquake.epicenterCity.ifBlank { "?" },
                    affectedCities.drop(1).joinToString(", ").ifBlank { "-" }
                )
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Earthquake>() {
        override fun areItemsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean =
            oldItem == newItem
    }
}