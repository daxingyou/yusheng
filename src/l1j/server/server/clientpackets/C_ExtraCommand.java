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
import l1j.server.server.model.Instance.L1PcInstance;
import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.serverpackets.S_DoActionGFX;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ExtraCommand extends ClientBasePacket {
	private static final String C_EXTRA_COMMAND = "[C] C_ExtraCommand";

	public C_ExtraCommand(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		int actionId = readC();
		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		if (pc.isInvisble()) { // 、中
			return;
		}
		if (pc.isTeleport()) { // 处理中
			return;
		}
		if (pc.hasSkillEffect(SHAPE_CHANGE)) { // 念为、变身中他送信
			return;
		}
		if (actionId<66) {
			return;
		}
		if (actionId>69) {
			return;
		}
		S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
		pc.broadcastPacket(gfx); // 周送信
	}

	@Override
	public String getType() {
		return C_EXTRA_COMMAND;
	}
}
