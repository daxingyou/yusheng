package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1WeaponEnchantTemp;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;
import l1j.william.New_Id;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WeaponEnchantDmgTable {
	private static final Log _log = LogFactory.getLog(WeaponEnchantDmgTable.class);

	private static final Map<Integer,L1WeaponEnchantTemp> _enchantmap = new HashMap<Integer,L1WeaponEnchantTemp>();
	
	private static WeaponEnchantDmgTable _instance;
	
	public static WeaponEnchantDmgTable get() {
		if (_instance == null) {
			_instance = new WeaponEnchantDmgTable();
		}
		
		return _instance;
	}
	
	public void reload(){
		_enchantmap.clear();
		load();
	}
	
	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `weapon_enchant_dmg`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int weaponId = rs.getInt("weaponId");
				final int enchantlevel = rs.getInt("enchantlevel");
				final int dmg = rs.getInt("dmg");
				
				L1WeaponEnchantTemp wEnchantTemp = _enchantmap.get(new Integer(weaponId));
				if (wEnchantTemp == null){
					wEnchantTemp = new L1WeaponEnchantTemp();
					_enchantmap.put(weaponId, wEnchantTemp);
				}
				if (enchantlevel > wEnchantTemp.get_maxenchantlevel()){
					wEnchantTemp.set_maxenchantlevel(enchantlevel);
				}
				wEnchantTemp.put_dmg(enchantlevel, dmg);
				
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入武器强化加成数量: " + _enchantmap.size() + "(" + timer.get() + "ms)");
	}
	/**
	 * 返回指定武器对应加成 攻击百分比
	 * @param weaponId
	 * @param enchantlevel
	 * @return
	 */
	public int getDmg(final int weaponId,final int enchantlevel){
		final L1WeaponEnchantTemp wEnchantTemp = _enchantmap.get(new Integer(weaponId));
		if (wEnchantTemp == null){
			return 0;
		}
		return wEnchantTemp.get_dmg(enchantlevel);
	}
	
}
