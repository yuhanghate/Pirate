package com.yuhang.novel.pirate.repository.database;



import androidx.room.TypeConverter;
import java.util.Date;


/**
 * e-mail : 714610354@qq.com
 * time   : 2018/04/24
 * desc   : 转换
 * @author yuhang
 */
public class ConvertersFactory {
  @TypeConverter
  public static Date fromTimestamp(Long value) {
    return value == null ? null : new Date(value);
  }

  @TypeConverter
  public static Long dateToTimestamp(Date date) {
    return date == null ? null : date.getTime();
  }
}
