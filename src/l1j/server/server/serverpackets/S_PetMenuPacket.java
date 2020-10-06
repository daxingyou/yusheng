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
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;

public class S_PetMenuPacket extends ServerBasePacket {


	public S_PetMenuPacket(L1NpcInstance npc, int exppercet) {
		buildpacket(npc, exppercet);
	}

	private void buildpacket(L1NpcInstance npc, int exppercet) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);

		if (npc instanceof L1PetInstance) { // 
			L1PetInstance pet = (L1PetInstance) npc;
			writeD(pet.getId());
			writeS("anicom");
			writeC(0x00);
			writeH(10);
			switch (pet.getCurrentPetStatus()) {
			case 1:
				writeS("$469"); // 攻击态势
				break;
			case 2:
				writeS("$470"); // 防御态势
				break;
			case 3:
				writeS("$471"); // 休憩
				break;
			case 5:
				writeS("$472"); // 警戒
				break;
			default:
				writeS("$471"); // 休憩
				break;
			}
			writeS(Integer.toString(pet.getCurrentHp())); // 现在ＨＰ
			writeS(Integer.toString(pet.getMaxHp())); // 最大ＨＰ
			writeS(Integer.toString(pet.getCurrentMp())); // 现在ＭＰ
			writeS(Integer.toString(pet.getMaxMp())); // 最大ＭＰ
			writeS(Integer.toString(pet.getLevel())); // 

			// 名前文字数8超落
			// " "," "OK
			// String pet_name = pet.get_name();
			// if (pet_name.equalsIgnoreCase(" ")) {
			// pet_name = " ";
			// }
			// else if (pet_name.equalsIgnoreCase(" ")) {
			// pet_name = " ";
			// }
			// writeS(pet_name);
			writeS(""); // 名前表示不安定、非表示
			writeS("$611"); // 腹
			writeS(Integer.toString(exppercet)); // 经验值
			writeS(Integer.toString(pet.getLawful())); // 
		} else if (npc instanceof L1SummonInstance) { // 
			L1SummonInstance summon = (L1SummonInstance) npc;
			writeD(summon.getId());
			writeS("moncom");
			writeC(0x00);
			writeH(6); // 渡引数文字数模样
			switch (summon.get_currentPetStatus()) {
			case 1:
				writeS("$469"); // 攻击态势
				break;
			case 2:
				writeS("$470"); // 防御态势
				break;
			case 3:
				writeS("$471"); // 休憩
				break;
			case 5:
				writeS("$472"); // 警戒
				break;
			default:
				writeS("$471"); // 休憩
				break;
			}
			writeS(Integer.toString(summon.getCurrentHp())); // 现在ＨＰ
			writeS(Integer.toString(summon.getMaxHp())); // 最大ＨＰ
			writeS(Integer.toString(summon.getCurrentMp())); // 现在ＭＰ
			writeS(Integer.toString(summon.getMaxMp())); // 最大ＭＰ
			writeS(Integer.toString(summon.getLevel())); // 
			// writeS(summon.getNpcTemplate().get_nameid());
			// writeS(Integer.toString(0));
			// writeS(Integer.toString(790));
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
