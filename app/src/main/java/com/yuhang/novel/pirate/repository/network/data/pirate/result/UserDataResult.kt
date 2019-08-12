package com.yuhang.novel.pirate.repository.network.data.pirate.result

/**
 * id : 1335954ac8754fcc8887e81cb11bf5d9
 * username : 13111111114
 * tel : 13111111114
 * email : 714610354@qq.com
 * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzMwNDc4MTIsInVzZXJuYW1lIjoiMTMzNTk1NGFjODc1NGZjYzg4ODdlODFjYjExYmY1ZDkifQ.IoqbP_2-A46udqx11Pwn7uAyARyk_VIJAfSmFV_VN6k
 */
data class UserDataResult(
    val email: String,
    val id: String,
    val tel: String,
    val token: String,
    val username: String
)