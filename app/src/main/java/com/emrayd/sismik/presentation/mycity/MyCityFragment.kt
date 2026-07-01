package com.emrayd.sismik.presentation.mycity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrayd.sismik.R
import com.emrayd.sismik.databinding.FragmentMyCityBinding
import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.presentation.feed.EarthquakeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyCityFragment : Fragment() {

    private var _binding: FragmentMyCityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCityViewModel by viewModels()
    private lateinit var adapter: EarthquakeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupSwipeRefresh()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = EarthquakeAdapter(onItemClick = ::navigateToDetail)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MyCityFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonSelectCity.setOnClickListener {
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setPopUpTo(
                    findNavController().graph.startDestinationId,
                    inclusive = false,
                    saveState = true
                )
                .build()
            findNavController().navigate(R.id.settingsFragment, null, navOptions)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.sismik_primary)
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    when {
                        state.isCityNotSet -> showCityNotSetState()
                        state.earthquakes.isEmpty() && !state.isLoading -> showEmptyState()
                        else -> showEarthquakeList(state.earthquakes)
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRefreshing.collect { isRefreshing ->
                    binding.swipeRefreshLayout.isRefreshing = isRefreshing
                }
            }
        }
    }

    private fun showCityNotSetState() {
        binding.recyclerView.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.VISIBLE
        binding.textEmptyTitle.text = getString(R.string.my_city_no_city_title)
        binding.textEmptyMessage.text = getString(R.string.my_city_no_city_message)
        binding.buttonSelectCity.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.VISIBLE
        binding.textEmptyTitle.text = getString(R.string.my_city_no_earthquake_title)
        binding.textEmptyMessage.text = getString(
            R.string.my_city_no_earthquake_message, viewModel.uiState.value.userCity
        )
        binding.buttonSelectCity.visibility = View.GONE
    }

    private fun showEarthquakeList(earthquakes: List<Earthquake>) {
        binding.layoutEmptyState.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        adapter.submitList(earthquakes)
    }

    private fun navigateToDetail(earthquake: Earthquake) {
        val action = MyCityFragmentDirections.actionMyCityFragmentToDetailFragment(
            earthquakeId = earthquake.id,
            title = earthquake.title,
            magnitude = earthquake.magnitude.toFloat(),
            depth = earthquake.depth.toFloat(),
            latitude = earthquake.latitude.toFloat(),
            longitude = earthquake.longitude.toFloat(),
            closestCity = earthquake.closestCity,
            distanceKm = earthquake.closestCityDistanceKm.toFloat(),
            epochSeconds = earthquake.epochSeconds,
            epicenterCity = earthquake.epicenterCity,
            affectedCities = earthquake.closestCities.take(3).joinToString(", ")
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}