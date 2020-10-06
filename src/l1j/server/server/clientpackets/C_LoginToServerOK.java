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

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_LoginToServerOK extends ClientBasePacket {

	private static final String C_LOGIN_TO_SERVER_OK = "[C] C_LoginToServerOK";

	public C_LoginToServerOK(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		final int type = this.readC();
		final int button = this.readC();
		L1PcInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}
		try {
			switch (type) {
			case 255: // 全体チャット && Whisper
				switch (button) {
				case 95:
				case 127:
					pc.setShowWorldChat(true); // open
					pc.setCanWhisper(true); // open
					break;
					
				case 91:
				case 123:
					pc.setShowWorldChat(true); // open
					pc.setCanWhisper(false); // close
					break;
					
				case 94:
				case 126:
					pc.setShowWorldChat(false); // close
					pc.setCanWhisper(true); // open
					break;
					
				case 90:
				case 122:
					pc.setShowWorldChat(false); // close
					pc.setCanWhisper(false); // close
					break;
				}
				break;
				
			case 0: // 全体聊天
				if (button == 0) { // close
					pc.setShowWorldChat(false);
					
				} else if (button == 1) { // open
					pc.setShowWorldChat(true);
				}
				break;
				
			case 2: // 密语频道
				if (button == 0) { // close
					pc.setCanWhisper(false);
					
				} else if (button == 1) { // open
					pc.setCanWhisper(true);
				}
				break;
				
			case 6: // 买卖频道
				if (button == 0) { // close
					pc.setShowTradeChat(false);
					
				} else if (button == 1) { // open
					pc.setShowTradeChat(true);
				}
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public String getType() {
		return C_LOGIN_TO_SERVER_OK;
	}
}
