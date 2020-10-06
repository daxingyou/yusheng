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
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_TradeOK extends ClientBasePacket {

	private static final String C_TRADE_CANCEL = "[C] C_TradeOK";
	
	public C_TradeOK(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);

		L1PcInstance player = _client.getActiveChar();
		final L1Object object = L1World.getInstance().findObject(player.getTradeID());
		L1PcInstance trading_partner = null;
		if (object instanceof L1NpcInstance) {
			player.setTradeOk(true);
			final L1Trade trade = new L1Trade();
			trade.tradeOK(player);
			return;
		}
		if (object instanceof L1PcInstance) {
			trading_partner = (L1PcInstance)object;
		}
		if (trading_partner != null) {
			player.setTradeOk(true);

			if (player.getTradeOk() && trading_partner.getTradeOk()) // 共OK押
			{
				// (180 - 16)个未满成立。
				// 本来重（等）既持场合考虑。
				if (player.getInventory().getSize() < (180 - 16)
						&& trading_partner.getInventory().getSize() < (180 - 16)) // 互相手渡
				{
					//L1Trade trade = new L1Trade();
					final L1Trade trade = new L1Trade();
					trade.tradeOK(player);
				} else // 互手元戾
				{
					player.sendPackets(new S_ServerMessage(263)); // \f1一人持步最大180个。
					trading_partner.sendPackets(new S_ServerMessage(263)); // \f1一人持步最大180个。
					final L1Trade trade = new L1Trade();
					trade.tradeCancel(player);
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_TRADE_CANCEL;
	}

}
