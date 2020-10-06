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
//import l1j.server.server.ClientThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_BanClan extends ClientBasePacket {

	private static final String C_BAN_CLAN = "[C] C_BanClan";
	
	private static final Log _log = LogFactory.getLog(C_BanClan.class);

	public C_BanClan(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		String s = readS();

		L1PcInstance pc = _client.getActiveChar();
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			String clanMemberName[] = clan.getAllMemberNames();
			int i;
			if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、、血盟主
				for (i = 0; i < clanMemberName.length; i++) {
					if (pc.getName().toLowerCase().equals(s.toLowerCase())) { // 君主自身
						return;
					}
				}
				L1PcInstance tempPc = L1World.getInstance().getPlayer(s);
				if (tempPc != null) { // 中
					if (tempPc.getClanid() == pc.getClanid()) { // 同
						tempPc.setClanid(0);
						tempPc.setClanname("");
						tempPc.setClanRank(0);
						tempPc.save(); // DB情报书迂
						clan.delMemberName(tempPc.getName());
						tempPc.sendPackets(new S_ServerMessage(238, pc
								.getClanname())); // %0血盟追放。
						pc.sendPackets(new S_ServerMessage(240, tempPc
								.getName())); // %0血盟追放。
						if (clan.getWarehouseUsingChar() == tempPc.getId()){
							clan.setWarehouseUsingChar(0);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(109, s)); // %0名前人。
					}
				} else { // 中
					try {
						L1PcInstance restorePc = CharacterTable.getInstance()
								.restoreCharacter(s);
						if (restorePc != null
								&& restorePc.getClanid() == pc.getClanid()) { // 同
							restorePc.setClanid(0);
							restorePc.setClanname("");
							restorePc.setClanRank(0);
							restorePc.save(); // DB情报书迂
							clan.delMemberName(restorePc.getName());
							pc.sendPackets(new S_ServerMessage(240, restorePc
									.getName())); // %0血盟追放。
						} else {
							pc.sendPackets(new S_ServerMessage(109, s)); // %0名前人。
						}
					} catch (Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
			}
		}
	}

	@Override
	public String getType() {
		return C_BAN_CLAN;
	}
}
