package com.yuhang.novel.pirate.repository.network

/**
 * URL地址环境
 * Created by yuhang on 11/07/2017.
 */
enum class NetType {
  /**
   * 开发环境
   */
  DEVELOP,

  /**
   * 上线版本
   */
  MASTER,

  /**
   * 测试版本
   */
  QA,

  /**
   * 预发布版本
   */
  STAGE,
}