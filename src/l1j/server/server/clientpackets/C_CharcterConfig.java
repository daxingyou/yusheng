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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CharacterConfigTable;
import l1j.server.server.mina.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_RequestDoors

public class C_CharcterConfig extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CharcterConfig.class);
	
	private static final String C_CHARCTER_CONFIG = "[C] C_CharcterConfig";

	public C_CharcterConfig(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		try {
			if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
//				L1PcInstance pc = client.getActiveChar();
				if (_client.getPcobjid()>0) {
					int length = readD() - 3;
					if (length > 900) {
						WriteLogTxt.Recording("异常快捷记录", "玩家OBJID："+_client.getPcobjid()+" IP为："+_client.getIp());
						return;
					}
					byte data[] = readByte();
					int count = CharacterConfigTable.getInstance()
							.countCharacterConfig(_client.getPcobjid());
					if (count == 0) {
						CharacterConfigTable.getInstance().storeCharacterConfig(_client.getPcobjid(), length, data);
					} else {
						CharacterConfigTable.getInstance().updateCharacterConfig(_client.getPcobjid(),length, data);
					}
				}		
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally{
			this.over();
		}
		
	}

	@Override
	public String getType() {
		return C_CHARCTER_CONFIG;
	}
}
