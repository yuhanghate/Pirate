package com.yuhang.novel.pirate.repository.network.data.pirate.result

/**
 * 版本升级
 */
data class VersionResult(
    val apkFileUrl: String,
    val constraint: Boolean,
    val newMd5: String,
    val newVersion: String,
    val targetSize: String,
    val update: String,
    val updateLog: String
)