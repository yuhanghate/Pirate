package com.yuhang.novel.pirate.ui.main.activity

import android.annotation.SuppressLint
import com.orhanobut.logger.Logger
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.base.BaseActivity
import com.yuhang.novel.pirate.databinding.ActivityMain2Binding
import com.yuhang.novel.pirate.eventbus.UpdateChapterEvent
import com.yuhang.novel.pirate.extension.findNavController
import com.yuhang.novel.pirate.ui.main.viewmodel.MainViewModel
import io.reactivex.Flowable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMain2Binding, MainViewModel>() {

    override fun onLayoutId(): Int {
        return R.layout.activity_main2
    }

    override fun onStatusColor(): Int {
        return android.R.color.white

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    override fun onDestroy() {
        onDestryEventbus(this)
        super.onDestroy()

    }

    override fun initView() {
        super.initView()

        onCreateEventbus(this)
        initUpdateChapterList()


        val nearby = mBinding.bottomBar.getTabWithId(R.id.tab_main)
//        nearby.setBadgeCount(5)

        val navController = findNavController(R.id.nav_host_fragment)
        mBinding.bottomBar.setOnTabSelectListener {

            when (it) {
                R.id.tab_main -> {
                    navController.navigate(R.id.mainFragment)
                }
                R.id.tab_store -> {
                    navController.navigate(R.id.storeFragment)
                }
                R.id.tab_me -> {
                    navController.navigate(R.id.meFragment)
                }
            }
        }

        mBinding.bottomBar.setOnTabReselectListener {
            when (it) {
                R.id.tab_main -> {
                    navController.navigate(R.id.mainFragment)
                }
                R.id.tab_store -> {
                    navController.navigate(R.id.storeFragment)
                }
                R.id.tab_me -> {
                    navController.navigate(R.id.meFragment)
                }
            }
        }
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


    override fun onBackPressed() {
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