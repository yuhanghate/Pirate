package com.yuhang.novel.pirate.viewholder

import android.view.ViewGroup
import com.yuhang.novel.pirate.base.BaseViewHolder
import com.yuhang.novel.pirate.constant.BookConstant
import com.yuhang.novel.pirate.databinding.ItemReadBookBinding
import com.yuhang.novel.pirate.extension.niceDp2px
import com.yuhang.novel.pirate.extension.niceSp2px
import com.yuhang.novel.pirate.listener.OnPageIndexListener
import com.yuhang.novel.pirate.repository.database.entity.BookContentKSEntity
import com.yuhang.novel.pirate.utils.StatusBarUtil


class ItemReadBookVH(parent: ViewGroup) :
        BaseViewHolder<BookContentKSEntity, ItemReadBookBinding>(parent, com.yuhang.novel.pirate.R.layout.item_read_book) {

    /**
     * 电池电量
     */
    var mBatteryLevel = 100

    fun setBatteryLevel(level:Int): ItemReadBookVH {
        mBatteryLevel = level
        return this
    }



    override fun onBindViewHolder(obj: BookContentKSEntity, position: Int) {
        super.onBindViewHolder(obj, position)
//        mBinding.contentTv.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0)
        mBinding.contentTv.position = position
        mBinding.contentTv.onClickNextListener = getListener()
        mBinding.contentTv.onClickPreviousListener = getListener()
        mBinding.contentTv.onClickCenterListener = getListener()

        obj.txtPage?.let { txtPage ->
            val margin = mContext.niceDp2px(20f)
            mBinding.contentTv.textSize = BookConstant.TEXT_PAGE_SIZE
            mBinding.contentTv.setTitle(txtPage.title)
                    .setPageTextColor(BookConstant.getPageTextColor())
                    .setBattery(mBatteryLevel)
                    .setMargin(margin, margin, 0, 0)
                    .setCurPage(txtPage)
                    .init()

        }

        mBinding.contentTv.post {
            //更新绘制
            mBinding.contentTv.invalidate()
        }



        //回调当前索引
//        getListener<OnPageIndexListener>()?.onPageIndexListener(position, obj.chapterId, obj.pid, obj.nid)
    }
}