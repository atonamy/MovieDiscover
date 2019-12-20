package com.transferwise.assignment.moviediscover.ui.controllers

import android.os.Handler
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.transferwise.assignment.moviediscover.data.model.Movie
import android.view.View
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.withState
import com.transferwise.assignment.moviediscover.*
import com.transferwise.assignment.moviediscover.view_model.MoviesViewModel
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList


class MoviesController(private val moviesViewModel: MoviesViewModel) :
    PagedListEpoxyController<Movie>(asyncHandler, asyncHandler) {



    class ControllerThreadExecutor : Executor {
        override fun execute(command: Runnable) {
            asyncHandler.post(command)
        }
    }

    companion object {
        val asyncHandler: Handler by lazy {EpoxyAsyncUtil.getAsyncBackgroundHandler()}
    }

    private val modelId: String
        get() = UUID.randomUUID().toString()
    private lateinit var onMovieClick: ((Movie) -> Unit)

    fun setOnMovieClickListener(listener: (Movie) -> Unit) {
        onMovieClick = listener
    }


    override fun buildItemModel(currentPosition: Int, item: Movie?): EpoxyModel<*> =
           RowMovieBindingModel_()
               .id(currentPosition)
               .imageBaseUrl(Settings.posterBaseUrl)
               .model(item ?: Movie(emptyList()))
               .onBind { _, view, _ ->
                       view.dataBinding.root.setOnClickListener {
                           if(item != null && ::onMovieClick.isInitialized)
                               onMovieClick(item)
                       }
               }
               .isLeft(currentPosition % 2 != 0)

    override fun addModels(models: List<EpoxyModel<*>>) = withState(moviesViewModel) {
        val header = ArrayList<EpoxyModel<*>>()
        when(it.movieListState) {
            is Loading ->
                if(models.isEmpty())
                    renderSkeleton()
                else
                    header.add(buildLoading())

            is Fail -> header.add(buildError(it.movieListState.error.message))
        }
        super.addModels(models + header)
    }

    private fun renderSkeleton()  {
            for(i in 1 .. 20)
                rowMovieLoading {
                    id(modelId)
                }
    }

    private fun buildLoading(): EpoxyModel<*> =
        RowLoadingBindingModel_().
            id(modelId).
            spanSizeOverride { _, _, _ ->
                2
            }

    private fun buildError(errorMessage: String?): EpoxyModel<*> =
        RowErrorBindingModel_().
            id(modelId).
            errorMessage(errorMessage).
            onRetryClick(View.OnClickListener {
                moviesViewModel.retrieveLastPage()
            }).
            spanSizeOverride { _, _, _ ->
                2
            }
}