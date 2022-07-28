package com.katic.rssfeedapp.ui.stories

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.katic.rssfeedapp.R
import com.katic.rssfeedapp.appComponent
import com.katic.rssfeedapp.data.model.RssItem
import com.katic.rssfeedapp.databinding.FragmentStoriesBinding
import com.katic.rssfeedapp.utils.UiUtils
import com.katic.rssfeedapp.utils.addOnRemovedListener
import com.katic.rssfeedapp.utils.viewModelProvider
import timber.log.Timber

class StoriesFragment : Fragment(), StoriesAdapter.Listener {

    private val args: StoriesFragmentArgs by navArgs()

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModelProvider {
        StoriesViewModel(appComponent.rssRepository, args.channelId)
    }

    private val storiesAdapter = StoriesAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMenu()

        binding.recycler.apply {
            val llm = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            layoutManager = llm
            addItemDecoration(DividerItemDecoration(context, llm.orientation))
            adapter = storiesAdapter

            addOnRemovedListener { position ->
                val story =
                    storiesAdapter.getItemOnPosition(position) ?: return@addOnRemovedListener
                Snackbar.make(
                    view,
                    UiUtils.formatRemovedStoryMessage(context, story),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.undo) {
                    viewModel.undoRemove()
                }.show()
                viewModel.removeStory(story)
            }
        }

        observeViewModel()
    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_stories, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.mark_all_read -> {
                        viewModel.markAllStoriesRead()
                        true
                    }
                    R.id.filter_all -> {
                        viewModel.setFilter(StoriesViewModel.Filter.ALL)
                        true
                    }
                    R.id.filter_read -> {
                        viewModel.setFilter(StoriesViewModel.Filter.READ)
                        true
                    }
                    R.id.filter_unread -> {
                        viewModel.setFilter(StoriesViewModel.Filter.UNREAD)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        viewModel.rssChannelAndStoriesResult.observe(viewLifecycleOwner) { rssChannelAndStories ->
            Timber.d("rssChannelAndStoriesResult: $rssChannelAndStories")
            if (rssChannelAndStories == null) {
                return@observe
            }
            storiesAdapter.swapData(rssChannelAndStories.stories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //
    // StoriesAdapter.Listener
    //

    override fun onStorySelected(story: RssItem) {
        Timber.d("onStorySelected: $story")
        val directions =
            StoriesFragmentDirections.actionStoriesToStory(story.title, story.channelId!!)
        findNavController().navigate(directions)
    }
}