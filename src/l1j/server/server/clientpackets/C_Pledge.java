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
 * Author: ChrisLiu.2007.06.30
 */
package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Pledge;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Pledge extends ClientBasePacket {

	private static final String C_PLEDGE = "[C] C_Pledge";

	public C_Pledge(byte abyte0[], LineageClient _client) {
		super(abyte0);
		L1PcInstance l1pcinstance = _client.getActiveChar();

		if (l1pcinstance.getClanid() > 0) {
			L1Clan _clan = L1World.getInstance().getClan(
					l1pcinstance.getClanname());
			if (l1pcinstance.isCrown()) {
				l1pcinstance.sendPackets(new S_Pledge("pledgeM", l1pcinstance
						.getId(), _clan.getClanName(), _clan
						.getOnlineMembersFP(), _clan.getAllMembersFP()));
			} else {
				l1pcinstance.sendPackets(new S_Pledge("pledge", l1pcinstance
						.getId(), _clan.getClanName(), _clan
						.getOnlineMembersFP()));
			}
		} else {
			l1pcinstance.sendPackets(new S_Pledge("pledge", l1pcinstance
					.getId()));
		}
	}

	@Override
	public String getType() {
		return C_PLEDGE;
	}

}
