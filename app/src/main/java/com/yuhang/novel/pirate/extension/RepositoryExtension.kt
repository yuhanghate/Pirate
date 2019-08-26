package com.yuhang.novel.pirate.extension

import com.google.gson.Gson
import com.yuhang.novel.pirate.repository.DataRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody
import java.io.File

/**
 * String -> RequestBody对象进行post
 */
fun DataRepository.nicePart(key:String, str: String?): MultipartBody.Part {



    val part = RequestBody.create(
            MediaType.parse("multipart/form-data"), str
    )
    return Part.createFormData(key, str)
}

/**
 * 上传文件
 */
fun DataRepository.nicePart(
        key: String,
        value: File?
): MultipartBody.Part {

    val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), value)
    return Part.createFormData(key, value?.name, requestBody)
}

/**
 * 批量上传上传文件
 */
fun DataRepository.nicePartList(
        key: String,
        value: List<File>
): List<MultipartBody.Part> {
    return value.map {
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
        val createFormData = Part.createFormData(key, it.name, requestBody)
        createFormData
    }.toList()
}

/**
 * Post 上传json 格式
 */
fun DataRepository.niceBody(obj: Any): RequestBody {
    return RequestBody.create(MediaType.parse("application/json"), Gson().toJson(obj))
}

/**
 * 获取存放小说的目录id
 */
fun DataRepository.niceDir(bookid: Long): Int {
    if (bookid < 1000) {
        return 1
    }
  val dirId = bookid.toString()
      .substring(0, bookid.toString().length - 3)
      .toInt() + 1
  return dirId
}