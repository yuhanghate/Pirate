package com.yuhang.novel.pirate.repository.network.data.kanshu.result;

import java.util.List;

/**
 * 小说详情
 */
public class BookDetailsResult {

  /**
   * status : 1
   * info : success
   * data : {"Id":191602,"Name":"凡人修仙之仙界篇","Img":"fanrenxiuxianzhixianjiepian.jpg","Author":"忘语","Desc":"一个普通的山村穷小子，偶然之下，进入到当地的江湖小门派，成了一名记名弟子。他以这样的身份，如何在门派中立足？又如何以平庸的资质，进入到修仙者的行列？和其他巨枭魔头，仙宗仙师并列于山海内外？","CId":96,"CName":"武侠仙侠","LastTime":"7/6/2019 5:06:55 PM","FirstChapterId":9788879,"LastChapter":"第九百八十七章 突飞猛进","LastChapterId":10060432,"BookStatus":"连载","SameUserBooks":[{"Id":424,"Name":"凡人修仙传","Author":"忘语","Img":"424.jpg","LastChapterId":4807039,"LastChapter":"忘语新书《玄界之门》","Score":0},{"Id":191527,"Name":"六迹之梦魇宫","Author":"忘语","Img":"liujizhimengyangong.jpg","LastChapterId":9825707,"LastChapter":"第八十章 传说","Score":0},{"Id":425,"Name":"魔天记","Author":"忘语","Img":"425.jpg","LastChapterId":5018152,"LastChapter":"凡人仙界篇已经上传了!","Score":0},{"Id":58140,"Name":"玄界之门","Author":"忘语","Img":"xuanjiezhimen.jpg","LastChapterId":3488741,"LastChapter":"第四十四章 飞升之劫","Score":0}],"SameCategoryBooks":[{"Id":392871,"Name":"地球第一剑","Img":"diqiudiyijian.jpg","Score":0},{"Id":453605,"Name":"白蛇之我欲成仙","Img":"baishezhiwoyuchengxian.jpg","Score":0},{"Id":374900,"Name":"西游之鹏魔王","Img":"xiyouzhipengmowang.jpg","Score":0},{"Id":8004,"Name":"魔门败类","Img":"momenbailei.jpg","Score":0},{"Id":426615,"Name":"龙吟山河图","Img":"longyinshanhetu.jpg","Score":0},{"Id":443847,"Name":"神枪剑影侠魔道","Img":"shenqiangjianyingxiamodao.jpg","Score":0},{"Id":292822,"Name":"呆萌小沙弥","Img":"daimengxiaoshami.jpg","Score":0},{"Id":366122,"Name":"武侠BOSS之路","Img":"wuxiaBOSSzhilu.jpg","Score":0},{"Id":423851,"Name":"从前有间庙","Img":"congqianyoujianmiao.jpg","Score":0},{"Id":241450,"Name":"医品太子妃","Img":"yipintaizifei.jpg","Score":0},{"Id":435766,"Name":"逆行神话","Img":"nixingshenhua.jpg","Score":0},{"Id":446469,"Name":"仙道独尊","Img":"xiandaoduzun.jpg","Score":0}],"BookVote":{"BookId":191602,"TotalScore":899,"VoterCount":96,"Score":9.4}}
   */

  private int status;
  private String info;
  private BookDetailsDataResult data;

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

  public BookDetailsDataResult getData() {
    return data;
  }

  public void setData(BookDetailsDataResult data) {
    this.data = data;
  }

}
