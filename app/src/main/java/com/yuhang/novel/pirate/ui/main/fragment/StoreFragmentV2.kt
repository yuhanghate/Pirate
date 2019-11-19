package com.yuhang.novel.pirate.ui.main.fragment

import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseFragment
import com.yuhang.novel.pirate.databinding.FragmentStoreV2Binding
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModel
import com.yuhang.novel.pirate.ui.main.viewmodel.StoreViewModelV2

/**
 * 书城 v2
 */
class StoreFragmentV2 :BaseFragment<FragmentStoreV2Binding, StoreViewModelV2>(){

    companion object{
        fun newInstance(): StoreFragmentV2 {
            return StoreFragmentV2()
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.fragment_store_v2
    }

    override fun initView() {
        super.initView()

    }


}