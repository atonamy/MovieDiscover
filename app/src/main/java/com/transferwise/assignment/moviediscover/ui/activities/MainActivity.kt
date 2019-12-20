package com.transferwise.assignment.moviediscover.ui.activities

import android.os.Bundle
import android.view.Menu
import com.transferwise.assignment.moviediscover.R
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.transferwise.assignment.moviediscover.ui.Menuable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        return populateMenu()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> passToFragment<Fragment> {
                this.onOptionsItemSelected(item)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun removeMenu() = menu?.clear()

    fun populateMenu(): Boolean {
        var hasMenu = false
        if(menu == null)
            return hasMenu
        passToFragment<Menuable> {
            hasMenu = true
            menuInflater.inflate(menuResId, menu)
            true
        }
        return hasMenu
    }

    private inline fun <reified T> passToFragment(fragment: T.() -> Boolean) {
        run loop@{
            navHostFragment.childFragmentManager.fragments.forEach {
                if (it is T)
                    if (fragment(it))
                        return@loop
            }
        }
    }
}

