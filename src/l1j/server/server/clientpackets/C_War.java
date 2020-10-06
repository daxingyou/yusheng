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

package l1j.server.server.clientpackets;

import java.io.File;
import java.util.List;


//import l1j.server.server.ClientThread;
import l1j.server.server.WarTimeController;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1War;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

import com.ZHConterver;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_War extends ClientBasePacket {

	private static final String C_WAR = "[C] C_War";
	
	public C_War(byte abyte0[], LineageClient _client) throws Exception {
		super(abyte0);
		int type = readC();
		String s = readS();

		final L1PcInstance player = _client.getActiveChar();
		String playerName = player.getName();
		String clanName = player.getClanname();
		int clanId = player.getClanid();
		
		if (!player.isCrown()) { // 君主以外
			player.sendPackets(new S_ServerMessage(478)); // \f1战争布告。
			return;
		}
		if (clanId == 0) { // 未所属
			player.sendPackets(new S_ServerMessage(272)); // \f1战争血盟创设。
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(clanName);
		if (clan == null) { // 自见
			return;
		}

		if (player.getId() != clan.getLeaderId()) { // 血盟主
			player.sendPackets(new S_ServerMessage(478)); // \f1战争布告。
			return;
		}

		if (clanName.toLowerCase().equals(s.toLowerCase())) { // 自指定
			return;
		}
		
		if (clan.getHouseId() != 0) {
			player.sendPackets(new S_SystemMessage("有血盟小屋的无法宣战!"));
			return;
		}
		String emblem_file = String.valueOf(player.getClanid());
		File file = new File("emblem/" + emblem_file);
		if (!file.exists()) {
			player.sendPackets(new S_SystemMessage("没有盟辉的无法宣战!"));
			return;
		}

		L1Clan enemyClan = null;
		String enemyClanName = null;
		for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // 名
			if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
				enemyClan = checkClan;
				enemyClanName = checkClan.getClanName();
				break;
			}
		}
		if (enemyClan == null) {//如果没有找到 转换成简体再找一次
			final String newClanName = ZHConterver.convert(s, ZHConterver.SIMPLIFIED);
			for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // 名
				if (checkClan.getClanName().toLowerCase().equals(newClanName.toLowerCase())) {
					enemyClan = checkClan;
					enemyClanName = checkClan.getClanName();
					break;
				}
			}
		}
		
		if (enemyClan == null) {
			return;
		}

		boolean inWar = false;
		List<L1War> warList = L1World.getInstance().getWarList(); // 全战争取得
		for (L1War war : warList) {
			if (war.CheckClanInWar(clanName)) { // 自既战争中
				if (type == 0) { // 宣战布告
					player.sendPackets(new S_ServerMessage(234)); // \f1血盟战争中。
					return;
				}
				inWar = true;
				break;
			}
		}
		if (inWar) {
			return;
		}
		if (!inWar && (type == 2 || type == 3)) { // 自战争中以外、降伏终结
			return;
		}

		if (clan.getCastleId() != 0) { // 自城主
			if (type == 0) { // 宣战布告
				player.sendPackets(new S_ServerMessage(474)); // 城所有、他城取出来。
				return;
			} else if (type == 2 || type == 3) { // 降伏、终结
				return;
			}
		}

		if (enemyClan.getCastleId() == 0 && // 相手城主、自Lv15以下
				player.getLevel() <= 15) {
			player.sendPackets(new S_ServerMessage(232)); // \f115以下君主宣战布告。
			return;
		}

		if (enemyClan.getCastleId() != 0 && // 相手城主、自Lv25未满
				player.getLevel() < 25) {
			player.sendPackets(new S_ServerMessage(475)); // 攻城战宣言25达。
			return;
		}

		if (enemyClan.getCastleId() != 0) { // 相手城主
			int castle_id = enemyClan.getCastleId();
			if (WarTimeController.getInstance().isNowWar(castle_id)) { // 战争时间内
				L1PcInstance clanMember[] = clan.getOnlineClanMember();
				for (int k = 0; k < clanMember.length; k++) {
					if (L1CastleLocation.checkInWarArea(castle_id,
							clanMember[k])) {
						player.sendPackets(new S_ServerMessage(477)); // 含全血盟员城外出攻城战宣言。
						player.sendPackets(new S_SystemMessage("血盟成员"+clanMember[k].getName()+"在攻城区"));
						return;
					}
				}
				boolean enemyInWar = false;
				for (L1War war : warList) {
					if (war.CheckClanInWar(enemyClanName)) { // 相手既战争中
						if (type == 0) { // 宣战布告
							war.DeclareWar(clanName, enemyClanName);
							war.AddAttackClan(clanName);
						} else if (type == 2 || type == 3) {
							if (!war
									.CheckClanInSameWar(clanName, enemyClanName)) { // 自相手别战争
								return;
							}
							if (type == 2) { // 降伏
								war.SurrenderWar(clanName, enemyClanName);
							} else if (type == 3) { // 终结
								war.CeaseWar(clanName, enemyClanName);
							}
						}
						enemyInWar = true;
						break;
					}
				}
				if (!enemyInWar && type == 0) { // 相手战争中以外、宣战布告
					L1War war = new L1War();
					war.handleCommands(1, clanName, enemyClanName); // 攻城战开始
				}
			} else { // 战争时间外
				if (type == 0) { // 宣战布告
					player.sendPackets(new S_ServerMessage(476)); // 攻城战时间。
				}
			}
		} else { // 相手城主
			boolean enemyInWar = false;
			for (L1War war : warList) {
				if (war.CheckClanInWar(enemyClanName)) { // 相手既战争中
					if (type == 0) { // 宣战布告
						player.sendPackets(new S_ServerMessage(236,
								enemyClanName)); // %0血盟血盟战争拒绝。
						return;
					} else if (type == 2 || type == 3) { // 降伏终结
						if (!war.CheckClanInSameWar(clanName, enemyClanName)) { // 自相手别战争
							return;
						}
					}
					enemyInWar = true;
					break;
				}
			}
			if (!enemyInWar && (type == 2 || type == 3)) { // 相手战争中以外、降伏终结
				return;
			}

			// 攻城战场合、相手血盟主承认必要
			L1PcInstance enemyLeader = L1World.getInstance().getPlayer(
					enemyClan.getLeaderName());

			if (enemyLeader == null) { // 相手血盟主见
				player.sendPackets(new S_ServerMessage(218, enemyClanName)); // \f1%0血盟君主现在居。
				return;
			}

			if (type == 0) { // 宣战布告
				enemyLeader.setTempID(player.getId()); // 相手ID保存
				enemyLeader.sendPackets(new S_Message_YN(217, clanName,
						playerName)); // %0血盟%1血盟战争望。战争应？（Y/N）
			} else if (type == 2) { // 降伏
				enemyLeader.setTempID(player.getId()); // 相手ID保存
				enemyLeader.sendPackets(new S_Message_YN(221, clanName)); // %0血盟降伏望。受入？（Y/N）
			} else if (type == 3) { // 终结
				enemyLeader.setTempID(player.getId()); // 相手ID保存
				enemyLeader.sendPackets(new S_Message_YN(222, clanName)); // %0血盟战争终结望。终结？（Y/N）
			}
		}
	}

	@Override
	public String getType() {
		return C_WAR;
	}

}
