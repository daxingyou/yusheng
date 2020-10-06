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

import java.util.Iterator;
import java.util.StringTokenizer;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class L1ToPC implements L1CommandExecutor {

	private L1ToPC() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ToPC();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final int type = Integer.parseInt(tok.nextToken());
			if (type == 0){
				final int mapId = Integer.parseInt(tok.nextToken());
				final Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();
				L1World.getInstance().clearTopcItem();
				while (itr.hasNext()) {
					final L1PcInstance tagerpc = itr.next();
					if (tagerpc.getNetConnection() != null){
						if (mapId < 0 || tagerpc.getMapId() == mapId){
							L1World.getInstance().addTopcItem(tagerpc.getName(),tagerpc.getLevel());
						}
					}
				}
				pc.setPage(0);
				pc.getAction().action("topcpshow",0);
			}else{
				final L1PcInstance target = L1World.getInstance().getPlayer(tok.nextToken());
				if (target != null) {
					L1Teleport.teleport(pc, target.getX(), target.getY(),
							target.getMapId(), 5, false);
					pc.sendPackets(new S_SystemMessage((new StringBuilder())
							.append(arg).append("移动到玩家身边。").toString()));
				}else{
					pc.sendPackets(new S_SystemMessage("\\F2该玩家已不在线上"));
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " 0/1 地图ID(-1所有地图)/玩家名称。"));
			//pc.sendPackets(new S_PacketBox(S_PacketBox.CALL_SOMETHING));
		}
	}
}
