package com.emrayd.sismik.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrayd.sismik.R
import com.emrayd.sismik.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()
    private var isInitialLoad = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCityInput()
        setupMagnitudeSeekBar()
        setupNotificationSwitch()
        observeUiState()
    }

    private fun setupCityInput() {
        // Şehir listesini resources'tan al ve adapter'a ver
        val cities = resources.getStringArray(R.array.turkish_cities).toList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cities
        )
        binding.editCity.setAdapter(adapter)

        // Kutuya tıklanınca metni temizle → tüm şehir listesi açılır
        binding.editCity.setOnClickListener {
            binding.editCity.text.clear()
            binding.editCity.showDropDown()
        }

        // Odaklanınca da aynı davranış (klavyeyle sekme geçişi için)
        binding.editCity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.editCity.text.clear()
                binding.editCity.showDropDown()
            }
        }

        // Kullanıcı listeden bir şehir seçtiğinde otomatik kaydet
        binding.editCity.setOnItemClickListener { _, _, _, _ ->
            val selected = binding.editCity.text.toString()
            viewModel.saveCity(selected)
            Toast.makeText(
                requireContext(),
                getString(R.string.settings_city_saved, selected),
                Toast.LENGTH_SHORT
            ).show()
            binding.editCity.clearFocus()
        }

        // Kaydet butonuyla da manuel kayıt mümkün kalsın
        binding.buttonSaveCity.setOnClickListener {
            val city = binding.editCity.text.toString().trim()
            if (city.isBlank()) {
                binding.editCity.error = getString(R.string.settings_city_empty_error)
                return@setOnClickListener
            }
            // Girilen şehrin listede olup olmadığını kontrol et
            if (!cities.any { it.equals(city, ignoreCase = true) }) {
                binding.editCity.error = "Lütfen listeden bir şehir seçin"
                return@setOnClickListener
            }
            viewModel.saveCity(city)
            Toast.makeText(
                requireContext(),
                getString(R.string.settings_city_saved, city),
                Toast.LENGTH_SHORT
            ).show()
            binding.editCity.clearFocus()
        }
    }

    private fun setupMagnitudeSeekBar() {
        binding.seekBarMagnitude.max = 80
        binding.seekBarMagnitude.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textMagnitudeValue.text =
                    getString(R.string.settings_magnitude_value, progressToMagnitude(progress))
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (!isInitialLoad) {
                    viewModel.saveMinMagnitude(progressToMagnitude(seekBar?.progress ?: 30))
                }
            }
        })
    }

    private fun setupNotificationSwitch() {
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (!isInitialLoad) viewModel.saveNotificationsEnabled(isChecked)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    isInitialLoad = true

                    if (state.userCity.isNotBlank()) {
                        binding.editCity.setText(state.userCity)
                        binding.textCurrentCity.text =
                            getString(R.string.settings_current_city, state.userCity)
                        binding.textCurrentCity.visibility = View.VISIBLE
                    } else {
                        binding.textCurrentCity.visibility = View.GONE
                    }

                    binding.seekBarMagnitude.progress = magnitudeToProgress(state.minMagnitude)
                    binding.textMagnitudeValue.text =
                        getString(R.string.settings_magnitude_value, state.minMagnitude)
                    binding.switchNotifications.isChecked = state.notificationsEnabled

                    isInitialLoad = false
                }
            }
        }
    }

    private fun progressToMagnitude(progress: Int): Float = (progress / 10f) + 1.0f
    private fun magnitudeToProgress(magnitude: Float): Int =
        ((magnitude - 1.0f) * 10).toInt().coerceIn(0, 80)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private inner class CityAdapter(
        context: android.content.Context,
        private val allCities: List<String>
    ) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, allCities.toMutableList()) {

        override fun getFilter() = object : android.widget.Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                // Metin boşsa tüm listeyi göster, değilse filtrele
                val filtered = if (constraint.isNullOrBlank()) {
                    allCities
                } else {
                    allCities.filter { it.contains(constraint, ignoreCase = true) }
                }
                results.values = filtered
                results.count = filtered.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                addAll(results?.values as? List<String> ?: emptyList())
                if ((results?.count ?: 0) > 0) notifyDataSetChanged()
                else notifyDataSetInvalidated()
            }
        }
    }
}
