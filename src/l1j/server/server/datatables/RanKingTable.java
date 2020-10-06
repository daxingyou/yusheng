package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.RanKingStorage;
import l1j.server.server.utils.SQLUtil;

public class RanKingTable implements RanKingStorage {
	private static final Log _log = LogFactory.getLog(RanKingTable.class);
	@Override
	public String[] getEzpayRankingData() {
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[40];
        for (int n = 0;n < 40;n+=2){
        	a[n] = "无";
        	a[n + 1] = "0";
        }
        try {
            con =  L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT char_name,EzpayCount FROM characters WHERE AccessLevel = 0 and EzpayCount > 0 order by EzpayCount desc limit 20");
            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = String.valueOf(rs.getInt(2));
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}
	
	@Override
	public String[] getLevelRankingData(final int type) {
		int key1 = -1;
		int key2 = -1;
		String msg = "";
		switch (type) {
		case 0:
			key1 = 0;
			key2 = 1;
			msg = "王族";
			break;
		case 1:
			key1 = 61;
			key2 = 48;
			msg = "骑士";
			break;
		case 2:
			key1 = 138;
			key2 = 37;
			msg = "妖精";
			break;
		case 3:
			key1 = 734;
			key2 = 1186;
			msg = "黑妖";
			break;
		case 4:
			key1 = 2786;
			key2 = 2796;
			msg = "法师";
			break;
		case 5:
			key1 = 6658;
			key2 = 6661;
			msg = "龙骑士";
			break;
		default:
			break;
		}
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[22];
        
        a[20] = String.valueOf(Config.MAXLV);
        a[21] = msg;
        
        for (int n = 0;n < 20;n+=2){
        	a[n] = "无";
        	a[n + 1] = "0";
        }
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT char_name,level FROM characters WHERE AccessLevel = 0 AND Class= ? OR Class= ? order by Exp desc limit 10");
            pstm.setInt(1, key1);
            pstm.setInt(2, key2);
            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = String.valueOf(rs.getInt(2));
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}

	@Override
	public String[] getAllLevelRankingData() {
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[41];
        
        a[40] = String.valueOf(Config.MAXLV);
        
        for (int n = 0;n < 40;n+=2){
        	a[n] = "无";
        	a[n + 1] = "0";
        }
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT char_name,level FROM characters WHERE AccessLevel = 0 order by Exp desc limit 20");
            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = String.valueOf(rs.getInt(2));
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}
	@Override
	public String[] getWeaponRankingData() {
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[20];
        for (int n = 0;n < 10;n++){
        	a[n*2] = "无";
        	a[n*2 + 1] = "";
        }
        try {
        	con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT characters.char_name,character_items.enchantlvl,character_items.item_name FROM characters,character_items,weapon WHERE character_items.char_id = characters.objid and characters.AccessLevel = 0 and character_items.item_id = weapon.item_id and character_items.enchantlvl > 0 order by character_items.enchantlvl desc limit 10");
            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = "+" + String.valueOf(rs.getInt(2)) + " " + rs.getString(3);;
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}
	
	@Override
	public String[] getArmorRankingData() {
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[40];
        for (int n = 0;n < 40;){
        	a[n] = "无";
        	a[n + 1] = "";
        	n+=2;
        }
        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            pstm = con.prepareStatement("SELECT characters.char_name,character_items.enchantlvl,character_items.item_name FROM characters,character_items,armor WHERE character_items.char_id = characters.objid and characters.AccessLevel = 0 and character_items.item_id = armor.item_id and character_items.enchantlvl > 0 order by character_items.enchantlvl desc limit 20");

            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = "+" + String.valueOf(rs.getInt(2)) + " " + rs.getString(3);
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}

	@Override
	public String[] getPKRankingData() {
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[20];
        for (int n = 0;n < 10;n++){
        	a[n*2] = "无";
        	a[n*2 + 1] = "";
        }
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT char_name,PKcount FROM characters WHERE AccessLevel = 0 AND PKcount > 0 order by PKcount desc limit 10");
            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = String.valueOf(rs.getInt(2));
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}
	
	@Override
	public String[] getDeathRankingData() {
		Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final String[] a = new String[20];
        for (int n = 0;n < 10;n++){
        	a[n*2] = "无";
        	a[n*2 + 1] = "0";
        }
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT char_name,Deathcount FROM characters WHERE AccessLevel = 0 AND Deathcount > 0 order by Deathcount desc limit 10");
            rs = pstm.executeQuery();
            int n = 0;
            while (rs.next()) {
            	a[n] = rs.getString(1);
            	a[n + 1] = String.valueOf(rs.getInt(2));
            	n+=2;
            }
        }catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
		}
        return a;
	}
}
