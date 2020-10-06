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
package l1j.server.server.model.Instance;

import java.io.File;
import java.util.List;

import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;// 王族取得王冠后守城警卫消失 
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;
// 王族取得王冠后守城警卫消失 

public class L1CrownInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1CrownInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		boolean in_war = false;
		if (player.getClanid() == 0) { // 未所属
			player.sendPackets(new S_SystemMessage("你没有加入血盟."));
			return;
		}
		String playerClanName = player.getClanname();
		L1Clan clan = L1World.getInstance().getClan(playerClanName);
		if (clan == null) {
			player.sendPackets(new S_SystemMessage("你没有加入血盟."));
			return;
		}
		if (!player.isCrown()) { // 君主以外
			player.sendPackets(new S_SystemMessage("你不是君主."));
			return;
		}
		if (player.getTempCharGfx() != 0 && // 变身中
				player.getTempCharGfx() != 1) {
			player.sendPackets(new S_SystemMessage("你不能变身."));
			return;
		}
		if (player.getId() != clan.getLeaderId()) { // 血盟主以外
			player.sendPackets(new S_SystemMessage("你不是盟主."));
			return;
		}
		if (!checkRange(player)) { // 1以内
			player.sendPackets(new S_SystemMessage("你离王冠距离太远."));
			return;
		}
		if (clan.getCastleId() != 0) {
			// 城主
			// 城所有、他城取出来。
			player.sendPackets(new S_ServerMessage(474));
			return;
		}
		if (clan.getHouseId() != 0) {
			//第6种 有盟屋拿不了
			player.sendPackets(new S_SystemMessage("你血盟拥有血盟小屋."));
			return;
		}
		String emblem_file = String.valueOf(player.getClanid());
		File file = new File("emblem/" + emblem_file);
		if (!file.exists()) {
			player.sendPackets(new S_SystemMessage("你没有盟标."));
			return;
		}

		// 座标castle_id取得
		int castle_id = L1CastleLocation.getCastleId(getX(), getY(), getMapId());

		// 布告。但、城主居场合布告不要
		boolean existDefenseClan = false;
		L1Clan defence_clan = null;
		for (L1Clan defClan : L1World.getInstance().getAllClans()) {
			if (castle_id == defClan.getCastleId()) {
				// 元城主
				defence_clan = L1World.getInstance().getClan(defClan.getClanName());
				existDefenseClan = true;
				break;
			}
		}
		//boolean iserror = false;
		List<L1War> wars = L1World.getInstance().getWarList(); // 全战争取得
		for (L1War war : wars) {
			if (castle_id == war.GetCastleId()) { // 今居城战争
				in_war = war.CheckClanInWar(playerClanName);
				break;
			}
			//else {
				//iserror = true;
			//}
		}
		if (existDefenseClan && in_war == false) { // 城主居、布告场合
			player.sendPackets(new S_SystemMessage("你没有在战争中."));
			return;
		}
		//if (iserror) {
			//return;
		//}

		// clan_datahascastle更新、付
		if (existDefenseClan && defence_clan != null) { // 元城主居
			defence_clan.setCastleId(0);
			ClanTable.getInstance().updateClan(defence_clan);
			L1PcInstance defence_clan_member[] = defence_clan
					.getOnlineClanMember();
			for (int m = 0; m < defence_clan_member.length; m++) {
				if (defence_clan_member[m].getId() == defence_clan
						.getLeaderId()) { // 元城主君主
					defence_clan_member[m].sendPackets(new S_CastleMaster(0,
							defence_clan_member[m].getId()));
					defence_clan_member[m].broadcastPacket(new S_CastleMaster(
							0, defence_clan_member[m].getId()));
					break;
				}
			}
		}
		clan.setCastleId(castle_id);
		ClanTable.getInstance().updateClan(clan);
		player.sendPackets(new S_CastleMaster(castle_id, player.getId()));
		player.broadcastPacket(new S_CastleMaster(castle_id, player.getId()));

		// 员以外街强制
		int[] loc = new int[3];
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getClanid() != player.getClanid() && !pc.isGm()) {

				if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
					if (!pc.isDead()){
						// 旗内居
						loc = L1CastleLocation.getGetBackLoc(castle_id);
						int locx = loc[0];
						int locy = loc[1];
						short mapid = (short) loc[2];
						L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
					}
				}
			}
		}

		// 表示
		for (L1War war : wars) {
			if (war.CheckClanInWar(playerClanName) && existDefenseClan) {
				// 自参加中、城主交代
				war.WinCastleWar(playerClanName);
				break;
			}
		}
		L1PcInstance[] clanMember = clan.getOnlineClanMember();

		if (clanMember.length > 0) {
			// 城占据。
			S_ServerMessage s_serverMessage = new S_ServerMessage(643);
			for (L1PcInstance pc : clanMember) {
				pc.sendPackets(s_serverMessage);
			}
		}
		
		// 王族取得王冠后守城警卫消失 
		for (L1Object obj : L1World.getInstance().getAllNpcs()) {
			if (obj instanceof L1CastleGuardInstance) {
				L1CastleGuardInstance npc = (L1CastleGuardInstance) obj;
				if (L1CastleLocation.checkInWarArea(castle_id, npc)) {
					npc.allTargetClear();
					npc.deleteMe();
				}
			}
		}
		// 王族取得王冠后守城警卫消失  end

		// spawn
		L1WarSpawn warspawn = new L1WarSpawn();
		warspawn.SpawnTower(castle_id);

		// 消
		deleteMe();
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeWorldObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		removeAllKnownObjects();
	}

	private boolean checkRange(L1PcInstance pc) {
		return (getX() - 1 <= pc.getX() && pc.getX() <= getX() + 1
				&& getY() - 1 <= pc.getY() && pc.getY() <= getY() + 1);
	}
}
