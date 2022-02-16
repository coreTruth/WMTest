package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.transition.TransitionInflater
import com.example.myapplication.data.Photo
import com.example.myapplication.databinding.MainFragmentBinding
import com.example.myapplication.ui.PhotoAdapter
import com.example.myapplication.ui.PhotoComparator
import com.example.myapplication.ui.PhotoLoadStateAdapter
import com.example.myapplication.ui.asMergedLoadStates
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoAdapter: PhotoAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        setupAdapter()
        postponeEnterTransition()
        rvPhoto.doOnPreDraw {
            startPostponedEnterTransition()
        }
        setupListener()
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun setupAdapter() = with(binding) {
        photoAdapter = PhotoAdapter(::onMainBreedClick, PhotoComparator)
        rvPhoto.adapter = photoAdapter.withLoadStateHeaderAndFooter(
            header = PhotoLoadStateAdapter(photoAdapter),
            footer = PhotoLoadStateAdapter(photoAdapter)
        )
        lifecycleScope.launchWhenCreated {
            photoAdapter.loadStateFlow.collect {
                swipeRefresh.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            photoAdapter.loadStateFlow
                // Use a state-machine to track LoadStates such that we only transition to
                // NotLoading from a RemoteMediator load if it was also presented to UI.
                .asMergedLoadStates()
                // Only emit when REFRESH changes, as we only want to react on loads replacing the
                // list.
                .distinctUntilChangedBy { it.refresh }
                // Only react to cases where REFRESH completes i.e., NotLoading.
                .filter { it.refresh is LoadState.NotLoading }
                // Scroll to top is synchronous with UI updates, even if remote load was triggered.
                .collect { rvPhoto.scrollToPosition(0) }
        }
    }

    private fun setupListener() = with(binding) {
        swipeRefresh.setOnRefreshListener {
            photoAdapter.refresh()
        }
        btSearch.setOnClickListener {
            executeSearch()
        }
        etSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                executeSearch()
                true
            } else {
                false
            }
        }
        etSearchText.setOnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                executeSearch()
            }
            true
        }
    }

    private fun initViewModel() = with(viewModel) {
        mainPhotos.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                photoAdapter.submitData(it)
            }
        }
    }

    private fun executeSearch() {
        viewModel.searchPhotos(binding.etSearchText.text.toString())
        binding.swipeRefresh.isRefreshing = true
    }

    private fun onMainBreedClick(item: Photo, sharedImageView: ImageView, transitionName: String) {
        val direction: NavDirections =
            MainFragmentDirections.actionMainFragmentToDetailFragment(item)

        val extras = FragmentNavigatorExtras(
            sharedImageView to transitionName
        )

        findNavController().navigate(direction, extras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}