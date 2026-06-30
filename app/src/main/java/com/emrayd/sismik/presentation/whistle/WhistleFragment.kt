package com.emrayd.sismik.presentation.whistle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emrayd.sismik.R
import com.emrayd.sismik.databinding.FragmentWhistleBinding

class WhistleFragment : Fragment() {

    private var _binding: FragmentWhistleBinding? = null
    private val binding get() = _binding!!

    private val whistleManager = WhistleManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhistleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonWhistle.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    whistleManager.start()
                    binding.textWhistleStatus.text = getString(R.string.whistle_status_playing)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    whistleManager.stop()
                    binding.textWhistleStatus.text = getString(R.string.whistle_status_idle)
                    v.performClick()
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        whistleManager.stop()
        super.onDestroyView()
        _binding = null
    }
}