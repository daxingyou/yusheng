package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1EnchantDmgreduction;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnchantDmgReductionTable {
	private static final Log _log = LogFactory.getLog(EnchantDmgReductionTable.class);

    private static EnchantDmgReductionTable _instance;

    private static final Map<String, L1EnchantDmgreduction> _maps = new HashMap<String,L1EnchantDmgreduction>();
    private static final Map<Integer, Integer> _checkMaxEnchantLevelmaps = new HashMap<Integer,Integer>();

    public static EnchantDmgReductionTable get() {
        if (_instance == null) {
            _instance = new EnchantDmgReductionTable();
        }
        return _instance;
    }

    public void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PerformanceTimer timer = new PerformanceTimer();
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `enchant_dmgreduction`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int itemId = rs.getInt("itemId");
                final int enchantLevel = rs.getInt("enchantLevel");
                final int dmgreduction = rs.getInt("damageReduction");
                final int addac = rs.getInt("ac");
                final int addHp = rs.getInt("hp");
                final int addMp = rs.getInt("mp");
                final int addHpr = rs.getInt("hpr");
                final int addMpr = rs.getInt("mpr");
                final int addstr = rs.getInt("str");
                final int adddex = rs.getInt("dex");
                final int addIntel = rs.getInt("Intel");
                final int addcha = rs.getInt("cha");
                final int addwis = rs.getInt("wis");
                final int addcon = rs.getInt("con");
                final int addsp = rs.getInt("sp");
                final int addmr = rs.getInt("mr");
                
                final L1EnchantDmgreduction item = new L1EnchantDmgreduction();
                item.set_dmgReduction(dmgreduction);
                item.set_ac(addac);
                item.set_hp(addHp);
                item.set_mp(addMp);
                item.set_hpr(addHpr);
                item.set_mpr(addMpr);
                item.set_str(addstr);
                item.set_dex(adddex);
                item.set_Intel(addIntel);
                item.set_cha(addcha);
                item.set_wis(addwis);
                item.set_con(addcon);
                item.set_sp(addsp);
                item.set_mr(addmr);
                
                if (_checkMaxEnchantLevelmaps.containsKey(itemId)){
                	final Integer checkMaxEnchanrLevel = _checkMaxEnchantLevelmaps.get(itemId);
                	if (enchantLevel > checkMaxEnchanrLevel.intValue()){
                		_checkMaxEnchantLevelmaps.put(itemId, enchantLevel);
                	}
                }else{
                	_checkMaxEnchantLevelmaps.put(itemId, enchantLevel);
                }
                _maps.put(new StringBuilder().append(itemId).append(enchantLevel).toString(), item);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("載入enchant_dmgreduction數量: " +  _maps.size() + "(" + timer.get() + "ms)");
    }
    
    public L1EnchantDmgreduction getEnchantDmgReduction(final int itemId,final int enchantLevel){
    	if (_checkMaxEnchantLevelmaps.containsKey(itemId)){
    		final Integer checkMaxEnchanrLevel = _checkMaxEnchantLevelmaps.get(itemId);
    		int maxEnchantLevel = enchantLevel;
    		if(enchantLevel > checkMaxEnchanrLevel.intValue()){
    			maxEnchantLevel = checkMaxEnchanrLevel.intValue();
    		}
    		final String checkKey = new StringBuilder().append(itemId).append(maxEnchantLevel).toString(); 
    		if (_maps.containsKey(checkKey)){
    			return _maps.get(checkKey);
    		}
    	}
    	return null;
    }
}
