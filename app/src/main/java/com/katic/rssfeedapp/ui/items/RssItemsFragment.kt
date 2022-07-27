package com.katic.rssfeedapp.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.katic.rssfeedapp.appComponent
import com.katic.rssfeedapp.data.model.RssItem
import com.katic.rssfeedapp.databinding.FragmentRssItemsBinding
import com.katic.rssfeedapp.utils.viewModelProvider
import timber.log.Timber

class RssItemsFragment : Fragment(), RssItemsAdapter.Listener {

    private val args: RssItemsFragmentArgs by navArgs()

    private var _binding: FragmentRssItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModelProvider {
        RssItemsViewModel(appComponent.rssRepository, args.channelId)
    }

    private val rssItemsAdapter = RssItemsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRssItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.apply {
            val llm = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            layoutManager = llm
            addItemDecoration(DividerItemDecoration(context, llm.orientation))
            adapter = rssItemsAdapter
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.rssChannelAndItemsResult.observe(viewLifecycleOwner) { rssChannelAndItems ->
            Timber.d("rssChannelAndItemsResult: $rssChannelAndItems")
            if (rssChannelAndItems == null) {
                return@observe
            }
            rssItemsAdapter.swapData(rssChannelAndItems.items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //
    // ItemsAdapter.Listener
    //

    override fun onItemSelected(rssItem: RssItem) {
        Timber.d("onItemSelected: $rssItem")
        val story =
            RssItemsFragmentDirections.actionItemsToStory(rssItem.title, rssItem.channelId!!)
        findNavController().navigate(story)
    }
}