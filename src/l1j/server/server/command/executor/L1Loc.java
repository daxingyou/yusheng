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
package l1j.server.server.command.executor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class L1Loc implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Loc.class);
	
	private L1Loc() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Loc();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int locx = pc.getX();
			int locy = pc.getY();
			short mapid = pc.getMapId();
			int gab = L1WorldMap.getInstance().getMap(mapid)
					.getOriginalTile(locx, locy);
			int heading = pc.getHeading();
			int i = 1;
			for (L1PcInstance player  : L1World.getInstance().getAllPlayers()) {
				if (player.getMapId() == pc.getMapId()) {
					pc.sendPackets(new S_SystemMessage(i+". 玩家:{ " + player.getName() + " }、血盟:{ " + player.getClanname() + " }"));
					i++;
				}			
			}
			String msg = String.format("座标 (%d, %d, %d) #%d# %d", locx, locy, mapid,heading,
					gab);
			pc.sendPackets(new S_SystemMessage(msg));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
