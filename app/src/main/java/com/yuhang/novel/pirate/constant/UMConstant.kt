package com.yuhang.novel.pirate.constant

/**
 * 友盟统计相关
 */
object UMConstant {

    /**
     * 统计小说:名称|章节|时间|背景|字体大小
     */
    const val TYPE_BOOK = "read_book"

    object TypeBook{

        /**
         * 小说名称
         */
        const val BOOK_NAME = "book_name"

        /**
         * 章节名称
         */
        const val BOOK_CHANPTER = "book_chanpter"

        /**
         * 阅读时间(格式化以后)
         */
        const val BOOK_READ_TIME = "book_read_time"

        /**
         * 阅读时间戳,用来基础删选
         */
        const val BOOK_READ_TIME_STAMP = "book_read_time_stamp"

        /**
         * 小说背景
         */
        const val BOOK_BACKGROUND = "book_page_background"

        /**
         * 阅读文字大小
         */
        const val BOOK_FONT = "book_font"
    }

    /**
     * 登录事件
     */
    const val TYPE_LOGIN = "um_login"

    /**
     * 注册事件
     */
    const val TYPE_REGISTER = "um_register"

    /**
     * 主页 -> 书箱详情
     */
    const val TYPE_MAIN_ITEM_LONG_CLICK_DETAILS = "um_main_item_long_click_details"

    /**
     * 主页 -> 目录书箱
     */
    const val TYPE_MAIN_ITEM_LONG_CLICK_DIR_CHANPTER = "um_main_item_long_click_dir_chanpter"

    /**
     * 主页 -> 从书架删除收藏
     */
    const val TYPE_MAIN_ITEM_LONG_CLICK_DELETE = "um_main_item_long_click_delete"

    /**
     * 主页 -> 书架置顶
     */
    const val TYPE_MAIN_ITEM_LONG_CLICK_TOP = "um_main_item_long_click_top"

    /**
     * 主页 -> 点击搜索
     */
    const val TYPE_MAIN_CLICK_SEARCH = "um_main_click_search"

    /**
     * 搜索 -> 搜索按钮
     */
    const val TYPE_SEARCH_CLICK_UM_SEARCH_CLICK_SEARCHBTN = "um_search_click_searchbtn"

    /**
     * 搜索 -> 点击搜索结果页
     */
    const val TYPE_SEARCH_ITEM_CLICK = "um_search_item_click"

    /**
     * 书箱详情 -> 立即阅读
     */
    const val TYPE_DETAILS_CLICK_READ = "um_details_click_read"

    /**
     * 书箱详情 -> 移出书架
     */
    const val TYPE_DETAILS_CLICK_REMOVE_BOOKCASE = "um_details_click_remove_bookcase"

    /**
     * 书箱详情 -> 点击作者其他作品
     */
    const val TYPE_DETAILS_CLICK_AUTHOR_OTHER_BOOK = "um_details_click_author_other_book"

    /**
     * 书箱详情 -> 点击单独章节目录
     */
    const val TYPE_DETAILS_CLICK_DIR_CHANPTER = "um_details_click_dir_chanpter"

    /**
     * 书城 -> 点击书城列表
     */
    const val TYPE_STORE_CLICK_ITEM = "um_store_click_item"

    /**
     * 我的 -> 检测升级
     */
    const val TYPE_ME_CLICK_VERSION_CHECK = "um_me_click_version_check"

    /**
     * 我的 -> 最近浏览
     */
    const val TYPE_ME_CLICK_HISTORY = "um_me_click_history"

    /**
     * 我的 -> 分享应用
     */
    const val TYPE_ME_CLICK_APP_SHARE = "um_me_click_app_share"

    /**
     * 我的 -> 意见反馈
     */
    const val TYPE_ME_CLICK_FEEDBACK = "um_me_click_feedback"

    /**
     * 我的 -> 夜间模式/日间模式
     */
    const val TYPE_ME_CLICK_MODEL = "um_me_click_model"

    /**
     * 我的 -> 设置
     */
    const val TYPE_ME_CLICK_SETTINGS = "um_me_click_settings"

    /**
     * 版本更新 -> 点击更新
     */
    const val TYPE_VERSION_UPDATE_YES = "um_version_update_yes"

    /**
     * 版本更新 -> 点击取消
     */
    const val TYPE_VERSION_UPDATE_NO = "um_version_update_no"

    /**
     * 分享应用 -> 点击取消
     */
    const val TYPE_SHARE_APP_YES = "um_share_app_yes"

    /**
     * 分享应用 -> 点击取消
     */
    const val TYPE_SHARE_APP_NO = "um_share_app_no"

    /**
     * 设置 -> 翻页方式
     */
    const val TYPE_SETTINGS_PAGE_MODEL = "um_settings_page_model"

    /**
     * 设置 -> 阅读界面
     */
    const val TYPE_SETTINGS_READ_MODEL = "um_settings_read_model"

    /**
     * 设置 -> 免责声明
     */
    const val TYPE_SETTINGS_DISCLAIMER = "um_settings_disclaimer"

    /**
     * 设置 -> 清除缓存
     */
    const val TYPE_SETTINGS_CLEAN_CACHE = "um_settings_clean_cache"

    /**
     * 设置 -> 退出登录
     */
    const val TYPE_SETTINGS_CLICK_LOGOUT = "um_settings_click_logout"

    /**
     * 阅读页 -> 点击刷新
     */
    const val TYPE_READ_CLICK_REFRESH = "um_read_click_refresh"

    /**
     * 阅读页 -> 点击阅读目录
     */
    const val TYPE_READ_CLICK_DIR_CHANPTER = "um_read_click_dir_chanpter"
}