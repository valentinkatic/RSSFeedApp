package com.katic.rssfeedapp.ui.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.katic.rssfeedapp.appComponent
import com.katic.rssfeedapp.databinding.FragmentStoryBinding
import com.katic.rssfeedapp.utils.viewModelProvider
import timber.log.Timber

class StoryFragment : Fragment() {

    private val args: StoryFragmentArgs by navArgs()

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModelProvider {
        StoryViewModel(appComponent.rssRepository, args.channelId, args.title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.storyResult.observe(viewLifecycleOwner) { story ->
            Timber.d("storyResult: $story")
            if (story == null) {
                return@observe
            }

            binding.webView.loadData(imgStyle + story.description, null, null)

            val hasReadMoreBtn = story.link != null
            binding.btnReadMore.apply {
                isVisible = hasReadMoreBtn
                if (hasReadMoreBtn) setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(story.link)
                    }
                    startActivity(intent)
                } else {
                    setOnClickListener(null)
                }
            }
        }
    }

    private val imgStyle get() = "<style>img{display: inline; height: auto; max-width: 100%;}</style>"

}