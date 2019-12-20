package com.transferwise.assignment.moviediscover.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import com.airbnb.mvrx.BaseMvRxFragment
import com.transferwise.assignment.moviediscover.ui.activities.MainActivity


abstract class BaseFragment : BaseMvRxFragment() {

    private val parentActivity: MainActivity by lazy {
        requireActivity() as MainActivity
    }

    protected fun showMenu(show: Boolean) {
        if (show)
            parentActivity.populateMenu()
        else
            parentActivity.removeMenu()
    }

    protected fun setupToolbar(title: String, showBackButton: Boolean = false) {
        val activity = requireActivity()
        if(activity is AppCompatActivity) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
            activity.supportActionBar?.title = title
        }
    }
}