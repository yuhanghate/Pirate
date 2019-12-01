package com.yuhang.novel.pirate.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yuhang.novel.pirate.base.BaseFragment
import me.yokeyword.fragmentation.ISupportFragment

class ViewPagerAdapter(fm: FragmentManager, val title:List<String>, val fragments:List<BaseFragment<*,*>>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}