package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1ArmorSets;
import l1j.server.server.utils.SQLUtil;

public class ArmorSetTable {
	private static Logger _log = Logger.getLogger(ArmorSetTable.class.getName());

	private static ArmorSetTable _instance;

	private final List<L1ArmorSets> _armorSetList = new ArrayList<L1ArmorSets>();

	public static ArmorSetTable getInstance() {
		if (_instance == null) {
			_instance = new ArmorSetTable();
		}
		return _instance;
	}

	private ArmorSetTable() {
		load();
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM armor_set");
			rs = pstm.executeQuery();
			fillTable(rs);
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, "error while creating armor_set table", e);
		}
		finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillTable(ResultSet rs) throws SQLException {
		while (rs.next()) {
			final L1ArmorSets as = new L1ArmorSets();
			as.setId(rs.getInt("id"));
			as.setSets(rs.getString("sets"));
			as.setPolyId0(rs.getInt("polyid0"));
			as.setPolyId1(rs.getInt("polyid55"));
			as.setPolyId2(rs.getInt("polyid60"));
			as.setPolyId3(rs.getInt("polyid65"));
			as.setPolyId4(rs.getInt("polyid70"));
			as.setAc(rs.getInt("ac"));
			as.setHp(rs.getInt("hp"));
			as.setMp(rs.getInt("mp"));
			as.setHpr(rs.getInt("hpr"));
			as.setMpr(rs.getInt("mpr"));
			as.setMr(rs.getInt("mr"));
			as.setStr(rs.getInt("str"));
			as.setDex(rs.getInt("dex"));
			as.setCon(rs.getInt("con"));
			as.setWis(rs.getInt("wis"));
			as.setCha(rs.getInt("cha"));
			as.setIntl(rs.getInt("intl"));
			as.setHitModifier(rs.getInt("hit_modifier"));
			as.setDmgModifier(rs.getInt("dmg_modifier"));
			as.setBowHitModifier(rs.getInt("bow_hit_modifier"));
			as.setBowDmgModifier(rs.getInt("bow_dmg_modifier"));
			as.setSp(rs.getInt("sp"));
			as.setDefenseWater(rs.getInt("defense_water"));
			as.setDefenseWind(rs.getInt("defense_wind"));
			as.setDefenseFire(rs.getInt("defense_fire"));
			as.setDefenseEarth(rs.getInt("defense_earth"));
			as.setRegistfreeze(rs.getInt("resist_freeze"));
			as.setRegistSleep(rs.getInt("resist_sleep"));
			as.setRegistStan(rs.getInt("resist_stan"));
			as.setRegistStone(rs.getInt("resist_stone"));

			_armorSetList.add(as);
		}
	}

	public L1ArmorSets[] getAllList() {
		return _armorSetList.toArray(new L1ArmorSets[_armorSetList.size()]);
	}

}
