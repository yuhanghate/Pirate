//package com.yuhang.novel.pirate.extension
//
//import android.app.Service
//import com.yuhang.novel.pirate.base.BaseActivity
//import com.yuhang.novel.pirate.base.BaseFragment
//import com.yuhang.novel.pirate.base.BaseViewModel
//import io.reactivex.FlowableTransformer
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//
//fun <T> BaseViewModel.io_main(): FlowableTransformer<T, T> {
//    return FlowableTransformer { upstream ->
//        upstream
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//}
//
//fun <T> BaseActivity<*, *>.io_main(): FlowableTransformer<T, T> {
//    return FlowableTransformer { upstream ->
//        upstream
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//}
//
//fun <T> BaseFragment<*, *>.io_main(): FlowableTransformer<T, T> {
//    return FlowableTransformer { upstream ->
//        upstream
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//}
//
//fun <T> Service.io_main(): FlowableTransformer<T, T> {
//    return FlowableTransformer { upstream ->
//        upstream
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//    }
//}