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
import l1j.server.server.model.Getback;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Weather;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Restart extends ClientBasePacket {



	private static final String C_RESTART = "[C] C_Restart";

	public C_Restart(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		L1PcInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}
		if (pc.isDeathProcessing()) {
			return;
		}
		pc.save();
		if (!pc.isDead()) {
			return;
		}

		int[] loc;
		
		if (pc.getHellTime() > 0) {
			loc = new int[3];
			loc[0] = 32701;
			loc[1] = 32777;
			loc[2] = 666;
		} else {
			loc = Getback.GetBack_Location(pc, true);
		}

		pc.removeAllKnownObjects();
		pc.broadcastPacket(new S_RemoveObject(pc));

		pc.setCurrentHp(pc.getLevel());
		pc.set_food(5);
		pc.setDead(false);
		pc.setStatus(0);
		L1World.getInstance().moveVisibleObject(pc, loc[2]);
		pc.setX(loc[0]);
		pc.setY(loc[1]);
		pc.setMap((short) loc[2]);
		pc.sendPackets(new S_MapID(pc.getMapId(), pc));
		pc.broadcastPacket(new S_OtherCharPacks(pc));
		pc.sendPackets(new S_OwnCharPack(pc));
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.startHpRegeneration();
		pc.startMpRegeneration();
		pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));
		if (pc.getHellTime() > 0) {
			pc.beginHell(false);
		}
		if (pc.getPinkSec()>0) {
//			System.out.println("当前玩家"+pc.getName()+"粉名数值大于0粉名");
			pc.sendPackets(new S_PinkName(pc.getId(), pc.getPinkSec()));
		}
		// 新手村功能 
		if ((pc.getMapId() == 68 || pc.getMapId() == 69) && (pc.getCurrentHp() <= (pc.getMaxHp() / 2))) {
			pc.setCurrentHp(pc.getMaxHp());
			//pc.setCurrentMp(pc.getMaxMp());
			pc.sendPackets(new S_ServerMessage(77));
			pc.sendPackets(new S_SkillSound(pc.getId(), 830));
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		// 新手村功能  end
		pc.setdeadsec(600);
	}

	@Override
	public String getType() {
		return C_RESTART;
	}
}