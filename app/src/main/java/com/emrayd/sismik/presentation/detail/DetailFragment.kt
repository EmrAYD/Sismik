package com.emrayd.sismik.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emrayd.sismik.R
import com.emrayd.sismik.databinding.FragmentDetailBinding
import com.emrayd.sismik.util.magnitudeToColor
import com.emrayd.sismik.util.toReadableDate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        bindEarthquakeDetails()

        // Geri butonu
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val position = LatLng(args.latitude.toDouble(), args.longitude.toDouble())

        map.addMarker(
            MarkerOptions()
                .position(position)
                .title(args.title)
                .snippet("Büyüklük: ${args.magnitude}")
        )

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 7f))
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun bindEarthquakeDetails() {
        binding.apply {
            textTitle.text = args.title
            textMagnitudeValue.text = String.format("%.1f", args.magnitude)
            magnitudeBadge.setCardBackgroundColor(magnitudeToColor(args.magnitude))
            textDepthValue.text = getString(R.string.format_depth, args.depth.toDouble())
            textDateTimeValue.text = args.epochSeconds.toReadableDate()
            textCoordinatesValue.text = getString(
                R.string.format_coordinates, args.latitude, args.longitude
            )

            // Deprem merkezi
            textEpicenterValue.text = args.epicenterCity.ifBlank { "-" }

            // Etkilenen iller
            textAffectedCitiesValue.text = args.affectedCities.ifBlank { "-" }
        }
    }

    override fun onStart() { super.onStart(); binding.mapView.onStart() }
    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { binding.mapView.onPause(); super.onPause() }
    override fun onStop() { binding.mapView.onStop(); super.onStop() }
    override fun onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory() }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        googleMap = null
        super.onDestroyView()
        _binding = null
    }
}