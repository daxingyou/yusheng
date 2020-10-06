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
package l1j.server.server.datatables;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1Pc;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;
// IdFactory
public class PcTable 
{
	private static final Log _log = LogFactory.getLog(PcTable.class);
	private final HashMap<String, L1Pc> _clans = new HashMap<String, L1Pc>();
	private static PcTable _instance;
	public static PcTable getInstance() 
	{
		if (_instance == null) 
		{
			System.out.print("╠》正在读取 仓库...");
			PerformanceTimer timer = new PerformanceTimer();
			_instance = new PcTable();
			System.out.println("完成!\t\t耗时: " + timer.get() + "ms");
		}
		return _instance;
	}
	private PcTable() 
	{
		Connection con = null;
		//Connection con1 = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try 
		{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM accounts");
			rs = pstm.executeQuery();
			while (rs.next()) 
			{
				//载入血盟资料
				L1Pc clan = new L1Pc();
				// clan.SetClanId(clanData.getInt(1));
				clan.setAccountName(rs.getString("login"));
				/*clan.setLeaderId(rs.getInt(3));
				clan.setLeaderName(rs.getString(4));
				clan.setCastleId(rs.getInt(5));
				clan.setHouseId(rs.getInt(6));*/
				//载入盟友清单
				pstm1 = con.prepareStatement("SELECT char_name FROM characters WHERE account_name = ?");
				pstm1.setString(1, clan.getAccountName());
				rs1 = pstm1.executeQuery();
				while (rs1.next()) 
				{
					clan.addMemberName(rs1.getString(1));
				}
				//System.out.println(clan.getClanName() + " 血盟成员数:" + clan.getMemberSize());
				//载入血盟仓库物品
				clan.getDwarfInventory().loadItems();// クラン仓库のロード
				_clans.put(clan.getAccountName(), clan);
				L1World.getInstance().storePc(clan);
			}
			//System.out.println("载入玩家仓库总数: " + _clans.size());
		} catch (SQLException e) 
		{
			_log.error(e.getLocalizedMessage(), e);
		} finally 
		{
			SQLUtil.close(rs);
			SQLUtil.close(rs1);
			SQLUtil.close(pstm);
			SQLUtil.close(pstm1);
			SQLUtil.close(con);
		}
	}
	public L1Pc createPc(String AccountName,int id) 
	{
		for (L1Pc oldClans : L1World.getInstance().getAllPcs()) 
		{
			if (oldClans.getAccountName().equalsIgnoreCase(AccountName)) 
			{
				return null;
			}
		}
		L1Pc clan = new L1Pc();
		clan.setAccountName(AccountName);
		/*clan.setLeaderId(player.getId());
		clan.setLeaderName(player.getName());
		clan.setCastleId(0);
		clan.setHouseId(0);*/
		//Connection con = null;
		//PreparedStatement pstm = null;
		/*try 
		{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
				.prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setString(2, clan.getClanName());
			pstm.setInt(3, clan.getLeaderId());
			pstm.setString(4, clan.getLeaderName());
			pstm.setInt(5, clan.getCastleId());
			pstm.setInt(6, clan.getHouseId());
			pstm.execute();
		} catch (SQLException e) 
		{
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally 
		{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}*/
		L1World.getInstance().storePc(clan);
		_clans.put(AccountName, clan);
		//player.setClanid(clan.getClanId());
		//player.setClanname(clan.getClanName());
		//player.setClanRank(L1Pc.CLAN_RANK_PRINCE);
		clan.addMemberName(AccountName);
		/*try 
		{
			// DBにキャラクター情报を书き迂む
			player.save();
		} catch (Exception e) 
		{
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}*/
		return clan;
	}

	/*public static void openlock()
	{
		l1j.server.Config.FCHECKUSE = true;
		l1j.server.Config.MCHECKUSE = true;
	}*/
	
	/*public void updateClan(L1Pc clan) 
	{
		Connection con = null;
		PreparedStatement pstm = null;
		try 
		{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
				.prepareStatement("UPDATE clan_data SET clan_id=?, leader_id=?, leader_name=?, hascastle=?, hashouse=? WHERE clan_name=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setInt(2, clan.getLeaderId());
			pstm.setString(3, clan.getLeaderName());
			pstm.setInt(4, clan.getCastleId());
			pstm.setInt(5, clan.getHouseId());
			pstm.setString(6, clan.getClanName());
			pstm.execute();
		} catch (SQLException e) 
		{
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally 
		{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}*/

	/*public void deleteClan(String clan_name) 
	{
		L1Pc clan = L1World.getInstance().getClan(clan_name);
		if (clan == null) 
		{
			return;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try 
		{
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
				.prepareStatement("DELETE FROM clan_data WHERE clan_name=?");
			pstm.setString(1, clan_name);
			pstm.execute();
		} catch (SQLException e) 
		{
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally 
		{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		clan.L1DwarfInventory().clearItems();
		clan.L1DwarfInventory().deleteAllItems();
		L1World.getInstance().removeClan(clan);
		_clans.remove(clan.getClanId());
	}*/
	public L1Pc getTemplate(int clan_id)
	{
		return _clans.get(clan_id);
	}
}