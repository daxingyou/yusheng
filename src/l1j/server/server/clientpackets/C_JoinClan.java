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

import java.util.logging.Logger;


import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.FaceToFace;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_JoinClan extends ClientBasePacket {

	private static final String C_JOIN_CLAN = "[C] C_JoinClan";

	public C_JoinClan(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);

		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}

		L1PcInstance target = FaceToFace.faceToFace(pc);
		if (target != null) {
			JoinClan(pc, target);
		}
	}

	private void JoinClan(L1PcInstance player, L1PcInstance target) {
		if (!target.isCrown()) { // 相手以外
			player.sendPackets(new S_ServerMessage(92, target.getName())); // \f1%0。
			return;
		}

		int clan_id = target.getClanid();
		String clan_name = target.getClanname();
		if (clan_id == 0) { // 相手
			player.sendPackets(new S_ServerMessage(90, target.getName())); // \f1%0血盟创设状态。
			return;
		}

		L1Clan clan = L1World.getInstance().getClan(clan_name);
		if (clan == null) {
			return;
		}

		if (target.getId() != clan.getLeaderId()) { // 相手血盟主以外
			player.sendPackets(new S_ServerMessage(92, target.getName())); // \f1%0。
			return;
		}

		if (player.getClanid() != 0) { // 既加入济
			if (player.isCrown()) { // 自分君主
				String player_clan_name = player.getClanname();
				L1Clan player_clan = L1World.getInstance().getClan(
						player_clan_name);
				if (player_clan == null) {
					return;
				}

				if (player.getId() != player_clan.getLeaderId()) { // 自分血盟主以外
					player.sendPackets(new S_ServerMessage(89)); // \f1血盟加入。
					return;
				}

				if (player_clan.getCastleId() != 0 || // 自分城主保有
						player_clan.getHouseId() != 0) {
					player.sendPackets(new S_ServerMessage(665)); // \f1城所有状态血盟解散。
					return;
				}
			} else {
				player.sendPackets(new S_ServerMessage(89)); // \f1血盟加入。
				return;
			}
		}

		target.setTempID(player.getId()); // 相手ID保存
		target.sendPackets(new S_Message_YN(97, player.getName())); // %0血盟加入。承诺？（Y/N）
	}

	@Override
	public String getType() {
		return C_JOIN_CLAN;
	}
}
