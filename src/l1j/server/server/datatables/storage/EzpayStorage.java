package l1j.server.server.datatables.storage;

import java.util.Map;

/**
 * 網站購物資料
 */
public interface EzpayStorage {
	/**
	 * 傳回指定帳戶匯款資料
	 * @param loginName 帳號名稱
	 * @param id 流水號
	 * @return 
	 */
	public Map<Integer, int[]> ezpayInfo(final String loginName,final int type);
	/**
	 * 更新資料
	 * @param loginName 帳號名稱
	 * @param id ID
	 * @param pcname 領取人物
	 * @param ip IP
	 */
	public boolean update(final String loginName, final int id, final String pcname, final String ip,final int type);
	
	/**
	 * 传回指定账户宣传币资料
	 * @param loginName
	 * @return
	 */
	public Map<Integer, int[]> XuanChuanInfo(final String loginName);
	/**
	 * 更新领群宣传币
	 * @param loginName
	 * @param id
	 * @param pcname
	 * @param ip
	 * @return
	 */
	public boolean updateXuanChuan(String loginName, int id, String pcname, String ip);
	
}