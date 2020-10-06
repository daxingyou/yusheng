package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1CharacterBlessEnchant;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharacterBlessEnchantTable {
	private static final Log _log = LogFactory.getLog(CharacterBlessEnchantTable.class);

	private static CharacterBlessEnchantTable _instance;
	
	public static CharacterBlessEnchantTable get(){
		if (_instance == null){
			_instance = new CharacterBlessEnchantTable();
		}
		return _instance;
	}
	
	public ArrayList<L1CharacterBlessEnchant> load(final int objId) {
		final ArrayList<L1CharacterBlessEnchant> lists = new ArrayList<L1CharacterBlessEnchant>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `character_blessenchant` WHERE objId = ?");
			pstm.setInt(1, objId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int itemId = rs.getInt("itemId");
				final int count = rs.getInt("enchantCount");
				
				final L1CharacterBlessEnchant temp = new L1CharacterBlessEnchant();
				temp.set_ItemId(itemId);
				temp.set_count(count);
				
				lists.add(temp);
			}
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return lists;
	}

	public void updateBlessEnchant(final int objId, final L1CharacterBlessEnchant blessEnchant) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE character_blessenchant SET enchantCount = ? WHERE itemId = ? AND objId = ?");
			pstm.setInt(1, blessEnchant.get_count());
			pstm.setInt(2, blessEnchant.get_ItemId());
			pstm.setInt(3, objId);
			pstm.execute();
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void addBlessEnchant(final int objId, final L1CharacterBlessEnchant blessEnchant) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_blessenchant SET objId = ?, itemId = ?, enchantCount = ?");
			pstm.setInt(1, objId);
			pstm.setInt(2, blessEnchant.get_ItemId());
			pstm.setInt(3, blessEnchant.get_count());
		
			pstm.execute();
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
