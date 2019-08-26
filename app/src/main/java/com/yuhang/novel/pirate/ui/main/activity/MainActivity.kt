package com.yuhang.novel.pirate.ui.main.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityMain2Binding
import com.yuhang.novel.pirate.eventbus.LogoutEvent
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.ui.main.fragment.MainFragment
import com.yuhang.novel.pirate.ui.main.fragment.MeFragment
import com.yuhang.novel.pirate.ui.main.fragment.StoreFragment
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import io.reactivex.Flowable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMain2Binding, MainViewModel>() {
    //    lateinit var navController: NavController
    override fun onLayoutId(): Int {
        return R.layout.activity_main2
    }

    override fun onStatusColor(): Int {
        return android.R.color.white

    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        return findNavController(R.id.nav_host_fragment).navigateUp()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEventbus(this)
    }


    override fun onDestroy() {
        onDestryEventbus(this)
        super.onDestroy()

    }

    override fun initView() {
        super.initView()


        initUpdateChapterList()


        loadMultipleRootFragment(
            R.id.nav_host_fragment,
            intent.getIntExtra("tab_index", 0),
            MainFragment.newInstance(),
            StoreFragment.newInstance(),
            MeFragment.newInstance()
        )

//        val nearby = mBinding.bottomBar.getTabWithId(R.id.tab_main)
//        nearby.setBadgeCount(5)

        mBinding.bottomBar.setOnTabSelectListener {

            when (it) {
                R.id.tab_main -> showHideFragment(findFragment(MainFragment::class.java))
                R.id.tab_store -> showHideFragment(findFragment(StoreFragment::class.java))
                R.id.tab_me -> showHideFragment(findFragment(MeFragment::class.java))
            }
        }
//
        mBinding.bottomBar.setOnTabReselectListener {
            when (it) {
                R.id.tab_main -> showHideFragment(findFragment(MainFragment::class.java))
                R.id.tab_store -> showHideFragment(findFragment(StoreFragment::class.java))
                R.id.tab_me -> showHideFragment(findFragment(MeFragment::class.java))
            }
        }

        mBinding.bottomBar.getTabAtPosition(intent.getIntExtra("tab_index", 0)).callOnClick()
    }


    /**
     * 延迟3秒钟, 10分钟更新一次章节目录
     */
    @SuppressLint("CheckResult")
    private fun initUpdateChapterList() {
        Flowable.interval(3, 60 * 10, TimeUnit.SECONDS)
            .compose(bindToLifecycle())
            .subscribe({ mViewModel.updateChapterToDB() }, {
                Logger.i("")
            })
    }


    override fun onBackPressedSupport() {
        moveTaskToBack(false)
    }

    /**
     * 更新所有章节信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(obj: UpdateChapterEvent) {
        mViewModel.updateChapterToDB()
    }

}