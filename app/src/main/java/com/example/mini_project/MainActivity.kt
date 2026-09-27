package com.example.mini_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mini_project.fragments.HomeFragment
import com.example.mini_project.fragments.OrderHistoryFragment
import com.example.mini_project.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // Remembers where Order History was opened from
    var ordersOpenedFromProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav =
            findViewById<BottomNavigationView>(
                R.id.bottomNav
            )

        // Open Home when MainActivity starts
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }

        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    ordersOpenedFromProfile = false
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_order -> {
                    loadFragment(OrderHistoryFragment())
                    true
                }

                R.id.nav_profile -> {
                    ordersOpenedFromProfile = false
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }
}