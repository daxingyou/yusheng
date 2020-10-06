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
package l1j.server.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1ClanMember;
import l1j.server.server.world.L1World;

public class L1Clan {
	
	/*	*//** 聯盟一般 *//*
	public static final int CLAN_RANK_LEAGUE_PUBLIC = 2;
	*//** 聯盟 副君主*//*
	public static final int CLAN_RANK_LEAGUE_VICEPRINCE = 3;
	*//** 聯盟君主 *//*
	public static final int CLAN_RANK_LEAGUE_PRINCE = 4;
	*//** 聯盟見習 *//*
	public static final int CLAN_RANK_LEAGUE_PROBATION = 5;
	*//** 聯盟守護騎士 *//*
	public static final int CLAN_RANK_LEAGUE_GUARDIAN = 6;*/
	/** 見習 */
	public static final int CLAN_RANK_PROBATION = 1;
	/** 一般 */
	public static final int CLAN_RANK_PUBLIC = 2;	
	/** 守護騎士 */
	public static final int CLAN_RANK_GUARDIAN = 3;
	/** 君主 */
	public static final int CLAN_RANK_PRINCE = 4;

	private int _clanId;

	private String _clanName;

	private int _leaderId;

	private String _leaderName;

	private int _castleId;

	private int _houseId;

	private int _warehouse = 0;
	
	private Date _date;

	private final L1DwarfForClanInventory _dwarfForClan = new L1DwarfForClanInventory(
			this);

	private final CopyOnWriteArrayList<L1ClanMember> membersList = new CopyOnWriteArrayList<L1ClanMember>();

	public int getClanId() {
		return _clanId;
	}

	public void setClanId(int clan_id) {
		_clanId = clan_id;
	}

	public String getClanName() {
		return _clanName;
	}

	public void setClanName(String clan_name) {
		_clanName = clan_name;
	}

	public int getLeaderId() {
		return _leaderId;
	}

	public void setLeaderId(int leader_id) {
		_leaderId = leader_id;
	}

	public String getLeaderName() {
		return _leaderName;
	}

	public void setLeaderName(String leader_name) {
		_leaderName = leader_name;
	}

	public int getCastleId() {
		return _castleId;
	}

	public void setCastleId(int hasCastle) {
		_castleId = hasCastle;
	}

	public int getHouseId() {
		return _houseId;
	}

	public void setHouseId(int hasHideout) {
		_houseId = hasHideout;
	}

	public void addMemberName(L1ClanMember member) {
		if (!membersList.contains(member)) {
			membersList.add(member);
		}
	}

	public void delMemberName(String member_name) {
		for (L1ClanMember member : membersList) {
			if (member.getName().equals(member_name)) {
				membersList.remove(member);
			}
		}
	}

	public L1PcInstance[] getOnlineClanMember() // 中员
	{
		ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
		for (L1ClanMember member : membersList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(member.getName());
			if (pc != null && !onlineMembers.contains(pc)) {
				onlineMembers.add(pc);
			}
		}
		return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
	}
	
	public L1ClanMember[] getOfflineClanMember() // 中员
	{
		ArrayList<L1ClanMember> offlineMembers = new ArrayList<L1ClanMember>();
		for (L1ClanMember member : membersList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(member.getName());
			if (pc == null) {
				offlineMembers.add(member);
			}
		}
		return offlineMembers.toArray(new L1ClanMember[offlineMembers.size()]);
		
	}

	/*
	 * public L1PcInstance[] GetOfflineClanMember() // 中员（未实装） {
	 * offline_list.clear(); // CharactersID一致入 //
	 * 更拔 }
	 */

	public String getOnlineMembersFP() // FP means "For Pledge"
	{
		String result = "";
		for (L1ClanMember member : membersList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(member.getName());
			if (pc != null) {
				result = result + member.getName() + " ";
			}
		}
		return result;
	}

	public String getAllMembersFP() {
		String _result = "";
		for (L1ClanMember i : membersList) {
			_result = _result + i.getName() + " ";
		}
		return _result;
	}

	public String[] getAllMemberNames() {
		ArrayList<String> AllMemberNames = new ArrayList<String>();
		for (L1ClanMember member : membersList) {
			AllMemberNames.add(member.getName());
		}
		return AllMemberNames.toArray(new String[AllMemberNames.size()]);
	}
	
	public CopyOnWriteArrayList<L1ClanMember> getAllMembers() {
		return membersList;
	}

	public L1DwarfForClanInventory getDwarfForClanInventory() {
		return _dwarfForClan;
	}

	public int getWarehouseUsingChar() {
		return _warehouse;
	}

	public void setWarehouseUsingChar(int objid) {
		_warehouse= objid;
	}

	public int getOnlineClanMemberSize() {
		ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
		for (L1ClanMember member : membersList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(member.getName());
			if (pc != null && !onlineMembers.contains(pc)) {
				onlineMembers.add(pc);
			}
		}
		return onlineMembers.size();
	}
	
	public void setBirthDay(Date date)
	{
		_date = date;
	}

	public Date getBirthDay() {
		return _date;
	}
	
	private String _clanNote;

	public void setClanNote(final String text) {
		_clanNote = text;
	}

	public String getClanNote() {
		return _clanNote;
	}
	
	// 血盟技能
	private boolean _clanskill = false;

	/**
	 * 设置是否能启用血盟技能
	 * 
	 * @param boolean1
	 */
	public void set_clanskill(boolean boolean1) {
		this._clanskill = boolean1;
	}

	/**
	 * 是否能启用血盟技能
	 * 
	 * @return true有 false没有
	 */
	public boolean isClanskill() {
		return this._clanskill;
	}

	private int _skilllv = 0;
	
	public void setSkillILevel(final int skilllv){
		_skilllv = skilllv;
	}
	
	public int getSkillLevel(){
		return _skilllv;
	}
}
