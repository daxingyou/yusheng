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
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeHeading;

public class C_ChangeHeading extends ClientBasePacket {
	private static final String C_CHANGE_HEADING = "[C] C_ChangeHeading";

	public C_ChangeHeading(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		int heading = readC();

		L1PcInstance pc = _client.getActiveChar();
		if (heading>7) {
			return;
		}
		if (heading<0) {
			return;
		}
		// 攻要求间隔
/*		if (Config.CHECK_ATTACK_INTERVAL) {
			final int result = pc.speed_Attack().checkInterval(ACT_TYPE.ATTACK);
			if (result == AcceleratorChecker.R_DISPOSED) {
				// _log.error("要求角色攻击:速度异常(" + pc.getName() + ")");
				return;
			}
		}*/
		pc.setHeading(heading);
		//pc.sendPackets(new S_ChangeHeading(pc));
/*		WriteLogTxt.Recording(
				"转向记录","玩家#"+pc.getName()
				+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
		        +"#转向 "
				+"("+heading+")。"
				);*/

		//_log.finest("Change Heading : " + pc.getHeading());

		if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
			pc.broadcastPacket(new S_ChangeHeading(pc));
		}
	}

	@Override
	public String getType() {
		return C_CHANGE_HEADING;
	}
}