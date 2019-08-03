package com.yuhang.novel.pirate.repository.network.data.kanshu.result;

import java.util.List;

public class BookSearchResult {

  /**
   * status : 1
   * info : success
   * data : [{"Id":"424","Name":"凡人修仙传","Author":"忘语","Img":"https://imgapi.jiaston.com/BookFiles/BookImages/424.jpg","Desc":"凡人修仙传最新章节列：小说《凡人修仙传》忘语/著,凡人修仙传全文阅读一个普通山村小子，偶然下进入到当地江湖小门派，成了一名记名弟子。他以这样身份，如何在门派中立足,如何以平庸的资质进入到修仙者的行列，从而......","BookStatus":"完结","LastChapterId":"4807039","LastChapter":"忘语新书《玄界之门》","CName":"武侠仙侠","UpdateTime":"2017-03-18 00:00:00"},{"Id":"55611","Name":"都市凡人修仙传","Author":"朝杨","Img":"https://imgapi.jiaston.com/BookFiles/BookImages/doushifanrenxiuxianchuan.jpg","Desc":"一个来自农村的少年，没有强大的背景，没有逆天的资质，在别人幻想着当富二代的时候，他却在为了成为富二代的爹努力奋斗，看一个平凡人物一步一步成长的故事","BookStatus":"连载","LastChapterId":"3090222","LastChapter":"关于本书","CName":"武侠仙侠","UpdateTime":"2017-03-01 00:00:00"},{"Id":"236188","Name":"小小凡人修仙传","Author":"至尊小宝","Img":"https://imgapi.jiaston.com/BookFiles/BookImages/xiaoxiaofanrenxiuxianchuan.jpg","Desc":"第629章大言明王\n　　  那个纨绔子弟以一副百事通的造型道：\u201c看起来朝中形势严峻，庄族拜相几百年，势力根深蒂固，长空豹又是王族正统，拥有得天独厚的影响力，暗中潜伏的东方一族也不容小视，而卫族、黄族隔岸观火。更传言，张夜虽然和大木族结亲，但也后院起火，不知道出了什么变故，张夜和妻子木昭君势同水火，放着那样的才女佳人却不碰，肯定有原因。如此，等于和木族对敌了。而张","BookStatus":"连载","LastChapterId":"1279344","LastChapter":"第629章 大言明王","CName":"武侠仙侠","UpdateTime":"2018-03-05 00:00:00"},{"Id":"366798","Name":"凡人修仙传之赤心","Author":"彩虹文艺节","Img":"https://imgapi.jiaston.com/BookFiles/BookImages/fanrenxiuxianchuanzhichixin.jpg","Desc":"【【第三届网络原创文学现实主义题材征文大赛】参赛作品】本是一位二十一世纪的大学生，有着光明的前途，在一次意外的考古中，误触到一个惊天的秘密而穿越到洪荒另一个世界。本想在此就此了结一生，浑浑噩噩的度过此生，却不料到在遭遇一场大难以后，进去门派修行，接触到另一世界。这究竟是梦幻还是真实，巧得天命！究竟自己有何义务？","BookStatus":"连载","LastChapterId":"1873265","LastChapter":"第四十五章召开比武","CName":"玄幻奇幻","UpdateTime":"2018-12-19 00:00:00"},{"Id":"41311","Name":"凡人修仙传之配角传奇","Author":"轻语江湖","Img":"https://imgapi.jiaston.com/BookFiles/BookImages/fanrenxiuxianchuanzhipeijiaochuanqi.jpg","Desc":"凡人结束，感动未断。\n　　\n　　    犹记得那人前装酷，人后逗比的厉师兄，\n　　\n　　    犹记得那惊才艳艳，亦师亦友的大衍神君，\n　　\n　　    犹记得那古林精怪，却不能修炼的墨彩环，\n　　\n　　    而那只出现过一次名字的韩家老三韩铸，抑或有着自己的人生。\n　　\n　　    那为老魔取名的老张叔，又何尝没有自己的传奇。\n　　\n　　    他们是凡人配角，但在这些故事之中，他们是自己人生的主角，因为他们也是传奇","BookStatus":"连载","LastChapterId":"2360333","LastChapter":"完结之后的一点话","CName":"小说同人","UpdateTime":"2015-12-19 00:00:00"}]
   */

  private int status;
  private String info;
  private List<BookSearchDataResult> data;

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

  public List<BookSearchDataResult> getData() {
    return data;
  }

  public void setData(List<BookSearchDataResult> data) {
    this.data = data;
  }

}
