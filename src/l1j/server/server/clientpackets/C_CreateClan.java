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

import l1j.server.server.datatables.ClanTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateClan extends ClientBasePacket {

	private static final String C_CREATE_CLAN = "[C] C_CreateClan";

	public C_CreateClan(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		String s = readS().trim();
		int i = s.length();
		L1PcInstance pc = _client.getActiveChar();
		if (i > 16) {
			pc.sendPackets(new S_ServerMessage(98));
			return;
		}
		if (i < 1){
			return;
		}
		if (pc.isCrown()) { // 
			if (pc.getClanid() == 0) {

				for (L1Clan clan : L1World.getInstance().getAllClans()) { // 同名
					if (clan.getClanName().toLowerCase()
							.equals(s.toLowerCase())) {
						pc.sendPackets(new S_ServerMessage(99)); // \f1同名前血盟存在。
						return;
					}
				}
				if (pc.getInventory().consumeItem(40308, 30000)) {
					L1Clan clan = ClanTable.getInstance().createClan(pc,
							s); // 创设
					if (clan != null) {
						pc.sendPackets(new S_ServerMessage(84, s)); // \f1%0血盟创设。
					}
				}else {
					// 189：\f1金幣不足。
					pc.sendPackets(new S_ServerMessage(189));
				}		
			} else {
				pc.sendPackets(new S_ServerMessage(86)); // \f1血盟结成作成。
			}
		} else {
			pc.sendPackets(new S_ServerMessage(85)); // \f1血盟创设。
		}
	}

	@Override
	public String getType() {
		return C_CREATE_CLAN;
	}

}
