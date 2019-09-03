package com.yuhang.novel.pirate.constant

import android.graphics.Color
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.repository.preferences.PreferenceUtil

object BookConstant {

    /**
     * 文字转换
     * 简体 > 繁体
     *
     */
    const val TEXT_CONVERT_TYPE = "text_convert_type"

    /**
     * 章节内容文字大小
     */
    const val TEXT_PAGE_SIZE = 18f

    /**
     * 章节内容背影颜色
     */
    var TEXT_PAGE_BACKGROUND = Color.parseColor("#F6EFDD")

    /**
     * 是否第一次安装app
     */
    const val IS_FIRST_INSTALL = "is_first_install"

    /**
     * 下一页加载触发数
     */
    const val LOAD_MORE_COUNT = 3

    /**
     * 上一页加载触发数
     */
    const val LOAD_REFRESH_COUNT = 3

    /**
     * 阅读背景
     */
    fun getPageBackground(): Int {
        return Color.parseColor(PreferenceUtil.getString("page_color", "#F6EFDD"))
    }

    /**
     * 获取字体颜色
     */
    fun getPageTextColor(): Int {
        return Color.parseColor(PreferenceUtil.getString("page_text_color", "#212121"))
    }

    /**
     * 阅读页底部背景
     */
    fun getReadBookButton(): Int {
        val list = arrayListOf<Int>(R.drawable.bg_read_book_button1, R.drawable.bg_read_book_button2,
                R.drawable.bg_read_book_button3, R.drawable.bg_read_book_button4)
        return list[PreferenceUtil.getInt("page_color_index", 1)]
    }

    /**
     * 设置阅读背景
     */
    fun setPageBackground(position: Int) {
        when (position) {
            0 -> {
                PreferenceUtil.commitString("page_color", "#ffffff")
                PreferenceUtil.commitString("page_text_color", "#212121")
                PreferenceUtil.commitInt("page_color_index", position)
            }
            1 -> {
                PreferenceUtil.commitString("page_color", "#F6EFDD")
                PreferenceUtil.commitString("page_text_color", "#212121")
                PreferenceUtil.commitInt("page_color_index", position)
            }
            2 -> {
                PreferenceUtil.commitString("page_color", "#FFC9F1CF")
                PreferenceUtil.commitString("page_text_color", "#212121")
                PreferenceUtil.commitInt("page_color_index", position)
            }
            3 -> {
                PreferenceUtil.commitString("page_color", "#FF232533")
                PreferenceUtil.commitString("page_text_color", "#DEFFFFFF")
                PreferenceUtil.commitInt("page_color_index", position)
            }
        }
    }

    /**
     * 当前选择的页面颜色
     */
    fun getPageColorIndex(): Int {
        return PreferenceUtil.getInt("page_color_index", 1)
    }

    /**
     * 获取可选择背影颜色
     */
    fun getPageColorArray(): List<String> {
        return arrayListOf<String>("#ffffff", "#F6EFDD", "#FFC9F1CF", "#FF232533")
    }

    /**
     * 设置界面字体大小
     */
    fun setPageTextSize(size: Float) {
        PreferenceUtil.commitFloat("page_text_size", size)
    }

    /**
     * 获取字体大小
     */
    fun getPageTextSize(): Float {
        return PreferenceUtil.getFloat("page_text_size", 18f)
    }

    /**
     * 根据字体大小获取进度
     */
    fun getFontProgress(): Float {
        return when (getPageTextSize()) {
            15f -> 1f
            16f -> 2f
            17f -> 3f
            18f -> 4f
            19f -> 5f
            20f -> 6f
            21f -> 7f
            else -> 4f
        }
    }

}