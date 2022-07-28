package com.katic.rssfeedapp.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.data.model.RssChannel
import com.katic.rssfeedapp.databinding.FragmentFavoritesBinding
import com.katic.rssfeedapp.ui.home.HomeViewModel
import com.katic.rssfeedapp.utils.UiUtils
import com.katic.rssfeedapp.utils.addOnRemovedListener
import com.katic.rssfeedapp.utils.viewModelProviderActivity
import timber.log.Timber

class FavoritesFragment : Fragment(), RssChannelsAdapter.Listener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModelProviderActivity<HomeViewModel>()

    private val rssChannelsAdapter = RssChannelsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.apply {
            val llm = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            layoutManager = llm
            addItemDecoration(DividerItemDecoration(context, llm.orientation))
            adapter = rssChannelsAdapter

            addOnRemovedListener { position ->
                val channel =
                    rssChannelsAdapter.getChannelOnPosition(position) ?: return@addOnRemovedListener
                Snackbar.make(
                    view,
                    UiUtils.formatRemovedRssChannelMessage(context, channel),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.undo) {
                    viewModel.undoRemove()
                }.show()
                viewModel.removeRssChannel(channel)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.favoriteRssChannelResult.observe(viewLifecycleOwner) {
            Timber.d("result: $it")
            rssChannelsAdapter.swapData(it)
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
        val directions = FavoritesFragmentDirections.actionFavoritesToItems(channel.title, channel.id!!)
        findNavController().navigate(directions)
    }

    override fun onAddedToFavorites(channel: RssChannel) {
        Timber.d("onAddedToFavorites: $channel")
        viewModel.addToFavorites(channel)
    }
}