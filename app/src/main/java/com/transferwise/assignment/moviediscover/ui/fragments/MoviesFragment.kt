package com.transferwise.assignment.moviediscover.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState

import com.transferwise.assignment.moviediscover.R
import com.transferwise.assignment.moviediscover.data.state.MoviesState
import com.transferwise.assignment.moviediscover.data_source.MoviesDataSource.SortOption
import com.transferwise.assignment.moviediscover.ui.Menuable
import com.transferwise.assignment.moviediscover.ui.controllers.MoviesController
import com.transferwise.assignment.moviediscover.view_model.MoviesViewModel
import kotlinx.android.synthetic.main.fragment_movies.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MoviesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, Menuable {

    private lateinit var controller: MoviesController
    private val moviesViewModel: MoviesViewModel by fragmentViewModel()
    private val navController: NavController by lazy {
        findNavController()
    }

    override val menuResId = R.menu.sort_menu

    override fun invalidate() = withState(moviesViewModel) {
        refresher.isEnabled = it.canRefresh
        if(it.reloaded.value)
            initMovieList(it)
        controller.requestModelBuild()
    }

    override fun onRefresh() {
        refresher.isRefreshing = false
        sortBy(SortOption.SortByDefault)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movies, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMenu(true)
        setupToolbar(getString(R.string.app_name))
        initController()
        withState(moviesViewModel, ::initMovieList)
        initNavigation()
        refresher.setOnRefreshListener(this)
        moviesViewModel.requestMoviesList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_sort_by_revenue -> sortBy(SortOption.SortByRevenueDesc)
            R.id.action_sort_by_vote -> sortBy(SortOption.SortByVotesAverageDesc)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initController() {
        controller = MoviesController(moviesViewModel)
        body.setController(controller)
        body.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun initMovieList(state: MoviesState) = controller.submitList(state.movieList)

    private fun initNavigation() {
        controller.setOnMovieClickListener {
            navController.navigate(MoviesFragmentDirections.showMovieDetails(it.id, it.title))
        }
    }

    private fun sortBy(sort: SortOption) =
        moviesViewModel.sortBy(sort)
}
