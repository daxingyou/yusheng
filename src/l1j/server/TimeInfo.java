/*
 * 我一生中 最爱的女人
 * 在今天凌晨(2008/11/1) 从25楼跳下
 * 
 * 这一跳 我的心 碎到极点
 * 永远都合不起来了
 * 
 * 制作团对中 SQL的霞宝宝
 * 我的最爱
 * 
 * 在他最后对我提出要求时
 * 说了..."拉我"
 * 
 * 可是我拉不住他
 * 
 * 我还是在他的床上 放上了他的睡衣
 * 
 * 我只认为他出去了
 * 
 * 等等就回来了
 * 
 * 只是...永远等不到了
 * 
 * 仅把此核心 献给我深爱的女人
 * 
 * dexc 2008/11/02
 */
package l1j.server;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 系统时间资讯
 * @author dexc
 *
 */
public class TimeInfo {
	
	private static TimeInfo _instance;

	public static TimeInfo time() {
		if (_instance == null) {
			_instance = new TimeInfo();
		}
		return _instance;
	}

	/**
	 * <font color=#00800>时间资料的转换</font>
	 * @param ts Timestamp
	 * @return Calendar
	 */
	public Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}
	
	/**
	 * <font color=#00800>传回系统目前时间</font><BR>
	 * 目前应用范围:<BR>
	 * 人物删除时间<BR>
	 * 任何装备计时<BR>
	 * 攻城时间计算<BR>
	 *  
	 * @return Calendar
	 */
	public Calendar getNowTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		return cal;
	}
	
	/**
	 * <font color=#00800>取得系统时间</font>
	 * 
	 * @return 传出标准时间格式 yyyy/MM/dd HH:mm:ss
	 */
	public String getNow_YMDHMS() {

		String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(new Date());

		return nowDate;
	}
	

	/**
	 * <font color=#00800>取得系统时间(条件式规范)</font>
	 * @param i 要输出的方式<BR>
	 * 			i = 0 年月日<BR>
	 * 			i = 1 时分秒<BR>
	 * 			i = 2 年月日时分秒
	 * 			i = 3 分
	 * 			i = 4 时
	 *  
	 * @return 传出标准时间格式
	 * 			i = 0 yyyy-MM-dd<BR>
	 * 			i = 1 HH:mm:ss<BR>
	 * 			i = 2 yyyy-MM-dd HH:mm:ss
	 */
	public String getNow_YMDHMS(int i) {

		Calendar c = Calendar.getInstance();

		String nowDate[] = new String[6];

		nowDate[0] = String.valueOf(c.get(Calendar.YEAR));

		nowDate[1] = String.valueOf(c.get(Calendar.MONTH) + 1);
		if (nowDate[1].length() == 1) {
			nowDate[1] = "0" + nowDate[1];
		}

		nowDate[2] = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		if (nowDate[2].length() == 1) {
			nowDate[2] = "0" + nowDate[2];
		}

		nowDate[3] = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		if (nowDate[3].length() == 1) {
			nowDate[3] = "0" + nowDate[3];
		}

		nowDate[4] = String.valueOf(c.get(Calendar.MINUTE));
		if (nowDate[4].length() == 1) {
			nowDate[4] = "0" + nowDate[4];
		}

		nowDate[5] = String.valueOf(c.get(Calendar.SECOND));
		if (nowDate[5].length() == 1) {
			nowDate[5] = "0" + nowDate[5];
		}
		switch (i) {
		case 0:// 年月日
			return nowDate[0] + "-" + nowDate[1] + "-" + nowDate[2];
		case 1:// 时分秒
			return nowDate[3] + ":" + nowDate[4] + ":" + nowDate[5];
		case 2:// 年月日时分秒
			return nowDate[0] + "-" + nowDate[1] + "-" + nowDate[2] + " " + 
			nowDate[3] + ":" + nowDate[4] + ":" + nowDate[5];
		case 3:// 分
			return nowDate[4];
		case 4:// 时
			return nowDate[3];
		case 5:// 日
			return nowDate[2];
		}
		return null;
	}
	
	/**
	 * <font color=#00800>计时器启动时间</font>
	 */
	private long _begin = System.currentTimeMillis();

	/**
	 * <font color=#00800>设置计时器启动时间</font>
	 */
	public void reset() {
		_begin = System.currentTimeMillis();
	}

	/**
	 * <font color=#00800>传回计时器已使用时间</font>
	 * @return
	 */
	public long get() {
		return System.currentTimeMillis() - _begin;
	}
}
