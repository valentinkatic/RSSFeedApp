package com.katic.rssfeedapp.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katic.rssfeedapp.databinding.FragmentFeedBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateText()

        binding.buttonFirst.setOnClickListener {
            counter++
            updateText()
        }
    }

    private fun updateText() {
        binding.textviewFirst.text = "Counter: $counter"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}