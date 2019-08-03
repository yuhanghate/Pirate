package com.yuhang.novel.pirate.repository.network.data.kanshu.result;

import java.util.List;

public class BookCategoryResult {


  /**
   * status : 1
   * info : success
   * data : [{"Id":"1","Name":"玄幻奇幻","Count":121548},{"Id":"2","Name":"武侠仙侠","Count":48527},{"Id":"3","Name":"都市言情","Count":58547},{"Id":"4","Name":"历史军事","Count":13106},{"Id":"5","Name":"科幻灵异","Count":40236},{"Id":"6","Name":"网游竞技","Count":10854},{"Id":"7","Name":"女生频道","Count":127859},{"Id":"66","Name":"同人小说","Count":4686}]
   */

  private int status;
  private String info;
  private List<BookCategoryDataResult> data;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public List<BookCategoryDataResult> getData() {
    return data;
  }

  public void setData(List<BookCategoryDataResult> data) {
    this.data = data;
  }

}
