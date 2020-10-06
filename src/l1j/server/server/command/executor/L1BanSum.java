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

import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class L1BanSum implements L1CommandExecutor {

	private L1BanSum() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1BanSum();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			for (L1PcInstance tgpc : L1World.getInstance().getRecognizePlayer(pc)) {
				if (!tgpc.getPetList().isEmpty()) {
					Object[] petList = tgpc.getPetList().values().toArray();
					for (Object petObject : petList) {
						if (petObject instanceof L1PetInstance) { // 
							L1PetInstance pet = (L1PetInstance) petObject;
							pet.collect();
							pet.updatePet();
							tgpc.getPetList().remove(pet.getId());
							pet.setDead(true);
							pet.deleteMe();
						}
						if (petObject instanceof L1SummonInstance) {
							L1SummonInstance sum = (L1SummonInstance) petObject;
							sum.setSkillEffect(L1SkillId.RETURN_TO_NATURE, 2000);
						}
					}
				}			
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "  请输入。"));
		}
	}
}
