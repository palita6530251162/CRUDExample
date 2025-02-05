package com.example.crudexample.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.crudexample.AuthorFragment
import com.example.crudexample.BookFragment

class LibraryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AuthorFragment()
            1 -> BookFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}