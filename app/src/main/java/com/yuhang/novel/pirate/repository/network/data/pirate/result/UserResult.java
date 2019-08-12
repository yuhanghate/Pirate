package com.yuhang.novel.pirate.repository.network.data.pirate.result;

public class UserResult {


    /**
     * code : 200
     * msg : 登陆成功
     * data : {"id":"1335954ac8754fcc8887e81cb11bf5d9","username":"13111111114","tel":"13111111114","email":"714610354@qq.com","token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzMwNDc4MTIsInVzZXJuYW1lIjoiMTMzNTk1NGFjODc1NGZjYzg4ODdlODFjYjExYmY1ZDkifQ.IoqbP_2-A46udqx11Pwn7uAyARyk_VIJAfSmFV_VN6k"}
     */

    private int code;
    private String msg;
    private UserDataResult data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserDataResult getData() {
        return data;
    }

    public void setData(UserDataResult data) {
        this.data = data;
    }

}
