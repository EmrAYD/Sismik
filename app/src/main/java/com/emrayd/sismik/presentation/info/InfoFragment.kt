package com.emrayd.sismik.presentation.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.emrayd.sismik.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAccordion()
    }

    private fun setupAccordion() {
        binding.headerBefore.setOnClickListener {
            toggleSection(binding.contentBefore, binding.chevronBefore)
        }
        binding.headerDuring.setOnClickListener {
            toggleSection(binding.contentDuring, binding.chevronDuring)
        }
        binding.headerAfter.setOnClickListener {
            toggleSection(binding.contentAfter, binding.chevronAfter)
        }
        binding.headerTrapped.setOnClickListener {
            toggleSection(binding.contentTrapped, binding.chevronTrapped)
        }
        binding.headerKit.setOnClickListener {
            toggleSection(binding.contentKit, binding.chevronKit)
        }
    }

    private fun toggleSection(contentView: View, chevronView: View) {
        if (contentView.visibility == View.VISIBLE) {
            contentView.visibility = View.GONE
            chevronView.animate().rotation(0f).setDuration(200).start()
        } else {
            contentView.visibility = View.VISIBLE
            chevronView.animate().rotation(180f).setDuration(200).start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}