package com.katic.rssfeedapp.ui.items

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.katic.rssfeedapp.data.model.RssItem
import com.katic.rssfeedapp.databinding.FragmentRssItemsBinding
import com.katic.rssfeedapp.ui.home.HomeViewModel
import com.katic.rssfeedapp.utils.viewModelProviderActivity
import timber.log.Timber

class RssItemsFragment : Fragment(), RssItemsAdapter.Listener {

    private var _binding: FragmentRssItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModelProviderActivity<HomeViewModel>()

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
        viewModel.selectedRssChannel.observe(viewLifecycleOwner) {
            if (it.item == null) {
                return@observe
            }
            rssItemsAdapter.swapData(it.item)
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
        if (rssItem.link == null) return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(rssItem.link)
        }
        startActivity(intent)
    }
}