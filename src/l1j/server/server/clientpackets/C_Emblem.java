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

import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.WriteLogTxt;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Emblem;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Emblem extends ClientBasePacket {

	private static final String C_EMBLEM = "[C] C_Emblem";
	private static final Log _log = LogFactory.getLog(C_Emblem.class);

	public C_Emblem(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);

		L1PcInstance player = _client.getActiveChar();
		if (player.getClanid() != 0 && player.isCrown()) {
			String emblem_file = String.valueOf(player.getClanid());

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream("emblem/" + emblem_file);
				for (short cnt = 0; cnt < 384; cnt++) {
					fos.write(readC());
				}
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
				throw e;
			} finally {
				if (null != fos) {
					fos.close();
				}
				fos = null;
			}
			player.sendPackets(new S_Emblem(player.getClanid()));
			// player.broadcastPacket(new S_Emblem(player.getClanid()));
			L1World.getInstance().broadcastPacketToAll(
					new S_Emblem(player.getClanid()));
			WriteLogTxt.Recording(
					"上传盟辉记录","玩家#"+player.getName()
					+"在地图ID"+player.getMapId()+"X:"+player.getX()+"Y:"+player.getY()+"#玩家objid：<"+player.getId()+">"
			        +"上传盟辉。"
					);
		}
	}

	@Override
	public String getType() {
		return C_EMBLEM;
	}
}
