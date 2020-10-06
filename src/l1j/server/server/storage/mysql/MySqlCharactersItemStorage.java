/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1CharaterTrade;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MySqlCharactersItemStorage extends CharactersItemStorage {

	private static final Log _log = LogFactory
			.getLog(MySqlCharactersItemStorage.class);

	@Override
	public ArrayList<L1ItemInstance> loadItems(int objId) throws Exception {
		ArrayList<L1ItemInstance> items = null;

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			items = new ArrayList<L1ItemInstance>();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_items WHERE char_id = ?");
			pstm.setInt(1, objId);

			L1ItemInstance item;
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemId = rs.getInt("item_id");
				L1Item itemTemplate = ItemTable.getInstance().getTemplate(
						itemId);
				if (itemTemplate == null) {
					_log.info(String.format("item id:%d not found", itemId));
					continue;
				}
				item = new L1ItemInstance();
				item.set_char_objid(objId);
				item.setId(rs.getInt("id"));
				item.setItem(itemTemplate);
				item.setCount(rs.getInt("count"));
				item.setEquipped(rs.getInt("Is_equipped") != 0 ? true : false);
				item.setEnchantLevel(rs.getInt("enchantlvl"));
				item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
				item.set_durability(rs.getInt("durability"));
				item.setChargeCount(rs.getInt("charge_count"));
				item.setLastUsed(rs.getTimestamp("last_used"));
				item.setAttrEnchantKind(rs.getInt("attr_enchant_kind"));
				item.setAttrEnchantLevel(rs.getInt("attr_enchant_level"));
				item.setAttrEnchantCount(rs.getInt("attr_enchant_count"));
				item.setGamNo(rs.getInt("gamNo"));
				item.setGamNpcId(rs.getInt("gamNpcId"));
				item.setGamNpcName(rs.getString("gamNpcName"));
				item.setSeal(rs.getInt("seal") != 0 ? true : false);
				item.setOverSeal(rs.getTimestamp("overseal"));
				item.setBless(rs.getInt("bless"));
				item.setTradable(rs.getBoolean("isTradable"));
				item.setOndurability(rs.getBoolean("isOndurability"));
				item.set_time(rs.getTimestamp("time"));
				final int tradecharId = rs.getInt("tradeCharId");
				final String tradecharName = rs.getString("tradeCharName");
				final int level = rs.getInt("tradeCharLevel");
				final int tradeChartype = rs.getInt("tradeCharType");
				if (tradecharId > 0) {
					final L1CharaterTrade tradechar = new L1CharaterTrade();
					tradechar.set_char_objId(tradecharId);
					tradechar.setName(tradecharName);
					tradechar.setLevel(level);
					tradechar.set_Type(tradeChartype);
					item.setItemCharaterTrade(tradechar);
				}
				// 添加道具PVP E 栏位
				item.setUpdatePVP(rs.getInt("item_pvp")); // pvp
				item.setUpdatePVE(rs.getInt("item_pve")); // pve
				item.setUpdateHP(rs.getInt("item_hp")); // hp
				item.set_updateMP(rs.getInt("item_mp")); // mp
				item.set_updateHPR(rs.getInt("item_hpr")); // hpr
				item.set_updateMPR(rs.getInt("item_mpr")); // mpr
				item.set_updateDMG(rs.getInt("item_dmg")); // 近战伤害
				item.set_updateHOTDMG(rs.getInt("item_hotdmg")); // 近战命中
				item.set_updateBOWDMG(rs.getInt("item_bowdmg")); // 远程攻击
				item.set_updateHOTBOWDMG(rs.getInt("item_hotbowdmg")); // 远程命中
				item.set_updateSP(rs.getInt("item_sp")); // 魔攻
				// 添加道具PVP E 栏位
				item.getLastStatus().updateAll();
				items.add(item);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return items;
	}

	@Override
	public void storeItem(int objId, L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_items SET id = ?, item_id = ?, char_id = ?, item_name = ?, "
							+ "count = ?, is_equipped = 0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, "
							+ "last_used = ?, attr_enchant_kind = ?, attr_enchant_level = ?, attr_enchant_count = ?, gamNo = ?,  gamNpcId = ?, "
							+ "gamNpcName = ?,seal = ?,overseal = ?,bless = ?,isOndurability = ?,time = ?,tradeCharId = ?,tradeCharName = ?,tradeCharLevel=?,tradeCharType=?,"
							+ "item_pvp = ?,item_pve = ?, item_hp = ?, item_mp = ?, item_hpr = ?, item_mpr = ?, item_dmg = ?, item_hotdmg = ?, item_bowdmg = ?, item_hotbowdmg = ?,"
							+ "item_sp = ?");
			pstm.setInt(1, item.getId());
			pstm.setInt(2, item.getItem().getItemId());
			pstm.setInt(3, objId);
			pstm.setString(4, item.getItem().getName());
			pstm.setLong(5, item.getCount());
			pstm.setInt(6, item.getEnchantLevel());
			pstm.setInt(7, item.isIdentified() ? 1 : 0);
			pstm.setInt(8, item.get_durability());
			pstm.setInt(9, item.getChargeCount());
			pstm.setTimestamp(10, item.getLastUsed());
			pstm.setInt(11, item.getAttrEnchantKind());
			pstm.setInt(12, item.getAttrEnchantLevel());
			pstm.setInt(13, item.getAttrEnchantCount());
			pstm.setInt(14, item.getGamNo());
			pstm.setInt(15, item.getGamNpcId());
			pstm.setString(16, item.getGamNpcName());
			pstm.setInt(17, item.isSeal() ? 1 : 0);
			pstm.setTimestamp(18, item.getOverSeal());
			pstm.setInt(19, item.getBless());
			pstm.setBoolean(20, item.isOndurability());
			pstm.setTimestamp(21, item.get_time());
			if (item.getItemCharaterTrade() != null) {
				pstm.setInt(22, item.getItemCharaterTrade().get_char_objId());
				pstm.setString(23, item.getItemCharaterTrade().getName());
				pstm.setInt(24, item.getItemCharaterTrade().getLevel());
				pstm.setInt(25, item.getItemCharaterTrade().get_Type());
			} else {
				pstm.setInt(22, 0);
				pstm.setString(23, null);
				pstm.setInt(24, 0);
				pstm.setInt(25, -1);
			}
			pstm.setInt(26, item.getUpdatePVP());
			pstm.setInt(27, item.getUpdatePVP());
			pstm.setInt(28, item.getUpdateHP());
			pstm.setInt(29, item.get_updateMP());
			pstm.setInt(30, item.get_updateHPR());
			pstm.setInt(31, item.get_updateMPR());
			pstm.setInt(32, item.get_updateDMG());
			pstm.setInt(33, item.get_updateHOTDMG());
			pstm.setInt(34, item.get_updateBOWDMG());
			pstm.setInt(35, item.get_updateHOTBOWDMG());
			pstm.setInt(36, item.get_updateSP());
			pstm.execute();

		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		item.getLastStatus().updateAll();
	}

	@Override
	public void deleteItem(L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_items WHERE id = ?");
			pstm.setInt(1, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateItemId(L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE character_items SET item_id = ?,bless = ? WHERE id = ?");
			pstm.setLong(1, item.getItemId());
			pstm.setInt(2, item.getBless());
			pstm.setInt(3, item.getId());
			pstm.execute();
			item.getLastStatus().updateItemId();
			item.getLastStatus().updateBless();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateItemCount(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET count = ? WHERE id = ?",
				item.getCount());
		item.getLastStatus().updateCount();
	}

	@Override
	public void updateItemDurability(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET durability = ? WHERE id = ?",
				item.get_durability());
		item.getLastStatus().updateDuraility();
	}

	@Override
	public void updateItemChargeCount(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET charge_count = ? WHERE id = ?",
				item.getChargeCount());
		item.getLastStatus().updateChargeCount();
	}

	@Override
	public void updateItemEnchantLevel(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET enchantlvl = ? WHERE id = ?",
				item.getEnchantLevel());
		item.getLastStatus().updateEnchantLevel();
	}

	@Override
	public void updateItemEquipped(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET is_equipped = ? WHERE id = ?",
				(item.isEquipped() ? 1 : 0));
		item.getLastStatus().updateEquipped();
	}

	@Override
	public void updateItemIdentified(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET is_id = ? WHERE id = ?",
				(item.isIdentified() ? 1 : 0));
		item.getLastStatus().updateIdentified();
	}

	@Override
	public void updateItemDelayEffect(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET last_used = ? WHERE id = ?",
				item.getLastUsed());
		item.getLastStatus().updateLastUsed();
	}

	public void updateItemAttrEnchantKind(L1ItemInstance item) throws Exception {
		executeUpdate(
				item.getId(),
				"UPDATE character_items SET attr_enchant_kind = ? WHERE id = ?",
				item.getAttrEnchantKind());
		item.getLastStatus().updateAttrEnchantKind();
	}

	public void updateItemAttrEnchantCount(L1ItemInstance item)
			throws Exception {
		executeUpdate(
				item.getId(),
				"UPDATE character_items SET attr_enchant_count = ? WHERE id = ?",
				item.getAttrEnchantCount());
	}

	@Override
	public void updateItemAttrEnchantLevel(L1ItemInstance item)
			throws Exception {
		executeUpdate(
				item.getId(),
				"UPDATE character_items SET attr_enchant_level = ? WHERE id = ?",
				item.getAttrEnchantLevel());
		item.getLastStatus().updateAttrEnchantLevel();
	}

	@Override
	public void updateItemGamNo(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET gamNo = ? WHERE id = ?",
				item.getGamNo());
		item.getLastStatus().updateGamNo();
		;

	}

	@Override
	public void updateItemGamNpcId(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET gamNpcId = ? WHERE id = ?",
				item.getGamNpcId());
		item.getLastStatus().updateGamNpcId();
		;

	}

	@Override
	public void updateItemGamNpcName(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET gamNpcName = ? WHERE id = ?",
				item.getGamNpcName());
		item.getLastStatus().updateGamNpcName();

	}

	@Override
	public void updateItemSeal(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET seal = ? WHERE id = ?",
				(item.isSeal() ? 1 : 0));
		item.getLastStatus().updateSeal();

	}

	@Override
	public void updateItemOverSeal(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET overseal = ? WHERE id = ?",
				item.getOverSeal());
		item.getLastStatus().updateOverSeal();

	}

	@Override
	public void updateItemBless(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET bless = ? WHERE id = ?",
				item.getBless());
		item.getLastStatus().updateBless();

	}

	// 更新PVP
	@Override
	public void updateItemPVP(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET item_pvp = ? WHERE id = ?",
				item.getUpdatePVP());
		item.getLastStatus().updatePVP();
		;

	}

	// 更新PVE
	@Override
	public void updateItemPVE(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET item_pve = ? WHERE id = ?",
				item.getUpdatePVE());
		item.getLastStatus().updatePVE();
		;

	}

	private void executeUpdate(int objId, String sql, String gamNpcName)
			throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setString(1, gamNpcName);
			pstm.setInt(2, objId);
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}

	@Override
	public int getItemCount(int objId) throws Exception {
		int count = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_items WHERE char_id = ?");
			pstm.setInt(1, objId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return count;
	}

	private void executeUpdate(int objId, String sql, long updateNum)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setLong(1, updateNum);
			pstm.setInt(2, objId);
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void executeUpdate(int objId, String sql, Timestamp ts)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setTimestamp(1, ts);
			pstm.setInt(2, objId);
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateItemTradable(L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE character_items SET isTradable = ? WHERE id = ?");
			pstm.setBoolean(1, item.isTradable());
			pstm.setInt(2, item.getId());
			pstm.execute();
			item.getLastStatus().updateTradable();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateItemOndurability(L1ItemInstance item) throws SQLException {
		executeUpdate(item.getId(),
				"UPDATE character_items SET isOndurability = ? WHERE id = ?",
				item.isOndurability() ? 1 : 0);
		item.getLastStatus().updateOndurability();
	}

	@Override
	public void updateItemCharTrade(L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE character_items SET tradeCharId = ?,tradeCharName = ?,tradeCharLevel = ?,tradeCharType = ? WHERE id = ?");
			pstm.setInt(1, item.getItemCharaterTrade().get_char_objId());
			pstm.setString(2, item.getItemCharaterTrade().getName());
			pstm.setInt(3, item.getItemCharaterTrade().getLevel());
			pstm.setInt(4, item.getItemCharaterTrade().get_Type());
			pstm.setInt(5, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
