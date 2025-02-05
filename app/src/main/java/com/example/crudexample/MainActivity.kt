package com.example.crudexample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.crudexample.adapter.LibraryPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        viewPager = findViewById<ViewPager2>(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val pagerAdapter = LibraryPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Add page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // When BookFragment (position 1) is selected
                if (position == 1) {
                    // Find the BookFragment and refresh its data
                    val fragment = supportFragmentManager.findFragmentByTag("f1")
                    if (fragment is BookFragment) {
                        fragment.refreshAuthorData()
                    }
                }
            }
        })

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Authors"
                1 -> "Books"
                else -> null
            }
        }.attach()
    }
}