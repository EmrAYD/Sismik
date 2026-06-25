package com.emrayd.sismik.presentation.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrayd.sismik.R
import com.emrayd.sismik.databinding.FragmentFeedBinding
import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.domain.usecase.FilterEarthquakesUseCase.SortType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var adapter: EarthquakeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSortChips()
        setupSearchInput()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = EarthquakeAdapter(onItemClick = ::navigateToDetail)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FeedFragment.adapter
        }
    }

    private fun setupSortChips() {
        binding.chipMagnitudeDesc.setOnClickListener {
            viewModel.onSortTypeSelected(SortType.MAGNITUDE_DESC)
        }
        binding.chipMagnitudeAsc.setOnClickListener {
            viewModel.onSortTypeSelected(SortType.MAGNITUDE_ASC)
        }
        binding.chipDateDesc.setOnClickListener {
            viewModel.onSortTypeSelected(SortType.DATE_DESC)
        }
        binding.chipDateDesc.isChecked = true
    }

    private fun setupSearchInput() {
        binding.editSearchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchQueryChanged(s?.toString().orEmpty())
            }
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.earthquakes.isEmpty() && !state.isLoading) {
                        showEmptyState()
                    } else {
                        showEarthquakeList(state.earthquakes)
                    }

                    state.errorMessage?.let { message ->
                        binding.textError.text = message
                        binding.textError.visibility = View.VISIBLE
                    } ?: run {
                        binding.textError.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.textEmptyState.visibility = View.VISIBLE
    }

    private fun showEarthquakeList(earthquakes: List<Earthquake>) {
        binding.textEmptyState.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        adapter.submitList(earthquakes)
    }

    private fun navigateToDetail(earthquake: Earthquake) {
        val action = FeedFragmentDirections.actionFeedFragmentToDetailFragment(
            earthquakeId = earthquake.id,
            title = earthquake.title,
            magnitude = earthquake.magnitude.toFloat(),
            depth = earthquake.depth.toFloat(),
            latitude = earthquake.latitude.toFloat(),
            longitude = earthquake.longitude.toFloat(),
            closestCity = earthquake.closestCity,
            distanceKm = earthquake.closestCityDistanceKm.toFloat(),
            epochSeconds = earthquake.epochSeconds
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}