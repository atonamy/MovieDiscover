package com.transferwise.assignment.moviediscover.ui.fragments


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import com.transferwise.assignment.moviediscover.*
import com.transferwise.assignment.moviediscover.data.state.MovieDetailsState
import com.transferwise.assignment.moviediscover.view_model.MovieDetailsViewModel
import kotlinx.android.synthetic.main.fragment_movie_details.body

/**
 * A simple [Fragment] subclass.
 *
 */
class MovieDetailsFragment : BaseFragment() {

    private val movieDetailsViewModel: MovieDetailsViewModel by fragmentViewModel()
    private val args: MovieDetailsFragmentArgs by navArgs()

    override fun invalidate() = withState(movieDetailsViewModel) { state ->
        if(state.showContent)
            renderContent(state)
        else
            renderEvents(state)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movie_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMenu(false)
        setupToolbar(args.movieTitle, true)
        movieDetailsViewModel.requestMovieDetails(args.movieId)
    }


    private fun renderContent(state: MovieDetailsState) {
        body.withModels {
            when (state.movieRequest) {
                is Fail -> rowError {
                    id("error")
                    errorMessage(state.movieRequest.error.message)
                    onRetryClick(View.OnClickListener {
                        movieDetailsViewModel.reloadMovieDetails(args.movieId)
                    })
                }
                is Success ->  {
                    val movie = state.movieRequest()
                    rowMovieDetails {
                        id("result")
                        model(movie)
                        imageBaseUrl(Settings.posterDetailsUrl)
                        onTrailerClick(View.OnClickListener {
                            movieDetailsViewModel.requestTrailer()
                        })
                        onWebsiteClick(View.OnClickListener {
                            movieDetailsViewModel.requestWebsite()
                        })
                    }
                }
                else -> rowLoading { id("loading") }
            }
        }
    }

    private fun renderEvents(state: MovieDetailsState) {
        val trailer = state.trailer.value
        val website = state.website.value
        val error = state.error.value

        when {
            trailer.isNotEmpty() -> watchYoutubeVideo(trailer)
            website.isNotEmpty() -> launchUrl(website)
            error.isNotEmpty() -> Snackbar.make(body, error, Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun watchYoutubeVideo(id: String) {
        try {
            requireContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("vnd.youtube:$id")
                )
            )
        } catch (e: ActivityNotFoundException) {
            launchUrl("http://youtu.be/$id")
        }
    }

    private fun launchUrl(url: String) =
        CustomTabsIntent
            .Builder()
            .addDefaultShareMenuItem()
            .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            .build()
            .launchUrl(requireContext(), Uri.parse(url))

}
