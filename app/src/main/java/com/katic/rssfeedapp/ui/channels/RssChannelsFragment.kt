package com.katic.rssfeedapp.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.databinding.FragmentListBinding
import com.katic.rssfeedapp.ui.home.HomeViewModel
import com.katic.rssfeedapp.utils.UiUtils
import com.katic.rssfeedapp.utils.viewModelProviderActivity
import timber.log.Timber

class RssChannelsFragment : Fragment(), RssChannelsAdapter.Listener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModelProviderActivity<HomeViewModel>()

    private val rssChannelsAdapter = RssChannelsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.apply {
            val llm = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            layoutManager = llm
            addItemDecoration(DividerItemDecoration(context, llm.orientation))
            adapter = rssChannelsAdapter
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.rssChannelResult.observe(viewLifecycleOwner) {
            when {
                it.isLoading -> binding.progressBar.show()
                it.isError -> {
                    binding.progressBar.hide()
                    UiUtils.handleUiError(activity, it.getException())
                }
                else -> {
                    binding.progressBar.hide()
                    Timber.d("result: ${it.data}")
                    if (it.data != null) {
                        rssChannelsAdapter.swapData(it.data)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //
    // RssChannelsAdapter.Listener
    //

    override fun onChannelSelected(channel: RssChannel) {
        Timber.d("onChannelSelected: $channel")
        viewModel.setSelectedChannel(channel)
        val items = RssChannelsFragmentDirections.actionChannelToItems(channel.title)
        findNavController().navigate(items)
    }
}