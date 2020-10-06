package l1j.server.server.datatables.storage;

public interface RanKingStorage{

	/**
	 * 返回充值排行数据
	 * @return
	 */
	public String[] getEzpayRankingData();

	public String[] getLevelRankingData(final int type);
	
	public String[] getAllLevelRankingData();

	public String[] getWeaponRankingData();
	
	public String[] getArmorRankingData();
	
	public String[] getPKRankingData();
	
	public String[] getDeathRankingData();
}
