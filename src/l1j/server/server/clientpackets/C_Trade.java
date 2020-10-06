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
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.utils.FaceToFace;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Trade extends ClientBasePacket {

	private static final String C_TRADE = "[C] C_Trade";

	public C_Trade(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);

		L1PcInstance player = _client.getActiveChar();
		if (player.isGhost()) {
			return;
		}
		L1PcInstance target = FaceToFace.faceToFace(player);
		if (target == null) {
			return;
		}
		final L1Trade trade = new L1Trade();
		if (target.getTradeID()!=0) {
			//trade.tradeCancel(target);
			return;
		}
		if (player.getTradeID()!=0) {
			//trade.tradeCancel(player);
			return;
		}
		if (player.getTradeOk()) {
			trade.tradeCancel(player);
			return;
		}
		if (target != null) {
			player.setTradeID(target.getId());
			target.setTradeID(player.getId());
			target.sendPackets(new S_Message_YN(252, player.getName())); // %0%s取引望。取引？（Y/N）
		}
	}

	@Override
	public String getType() {
		return C_TRADE;
	}
}
