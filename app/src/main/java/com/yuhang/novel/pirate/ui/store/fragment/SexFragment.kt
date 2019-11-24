package com.yuhang.novel.pirate.ui.store.fragment

import android.content.Context
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentSexBinding
import com.yuhang.novel.pirate.ui.store.viewmodel.SexViewModel

class SexFragment:BaseFragment<FragmentSexBinding, SexViewModel>() {

    companion object{
        fun newInstance():SexFragment {
            return SexFragment()
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_sex
    }

    override fun initView() {
        super.initView()
    }
}