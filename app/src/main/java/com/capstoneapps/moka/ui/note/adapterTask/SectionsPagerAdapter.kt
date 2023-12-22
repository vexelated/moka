package com.capstoneapps.moka.ui.note.adapterTask

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstoneapps.moka.ui.note.fragment.HistoryFragment
import com.capstoneapps.moka.ui.note.fragment.TaskFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = TaskFragment()
            1 -> fragment = HistoryFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}