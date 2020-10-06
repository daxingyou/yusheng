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

import l1j.server.server.mina.LineageClient;
import l1j.server.server.serverpackets.S_PacketBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_NewCharSelect extends ClientBasePacket {
	private static final String C_NEW_CHAR_SELECT = "[C] C_NewCharSelect";
	private static final Log _log = LogFactory.getLog(C_NewCharSelect.class);

	public C_NewCharSelect(byte[] decrypt, LineageClient client) {
		super(decrypt);
		try {
//			Thread.sleep(250);
			if (client.getActiveChar() != null) {
				if (client.getActiveChar().getAttacksec()>0) {
					if (!client.getActiveChar().getMap().isSafetyZone(client.getActiveChar().getX(), client.getActiveChar().getY())) {
						client.sendPacket(new S_PacketBox(S_PacketBox.MSG_CANT_LOGOUT));
						return;
					}				
				}
				if (client.getActiveChar().isDeathProcessing()) {
					return;
				}
				_log.info("Disconnect from: " + client.getActiveChar().getName());
				synchronized (client.getActiveChar()) {
					LineageClient.quitGame(client.getActiveChar());
					client.getActiveChar().logout();
					client.setActiveChar(null);
				}
				client.sendPacket(new S_PacketBox(S_PacketBox.LOGOUT));
			} else {
				_log.info("Disconnect Request from Account : "
						+ client.getAccountName());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			client.CharReStart(true);
		}
		
	}

	@Override
	public String getType() {
		return C_NEW_CHAR_SELECT;
	}
}
