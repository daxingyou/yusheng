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

//import l1j.server.server.ClientThread;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Skills;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_SkillBuyOK extends ClientBasePacket {

	private static final String C_SKILL_BUY_OK = "[C] C_SkillBuyOK";

	public C_SkillBuyOK(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);

		int count = readH();
		int price = 100;
		int skillId = 0;

		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		boolean isGfx = false;
		for (int i = 0; i < count; i++) {
			skillId = readD() + 1;
			 // 检查是否已学习该法术
            if (!CharSkillReading.get().spellCheck(pc.getId(), skillId)) {
            	 // 取回技能资料
                final L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
                if (l1skills != null){
        			if (pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, price);
                        // 加入资料库
                        CharSkillReading.get().spellMastery(pc.getId(),l1skills.getSkillId(), l1skills.getName(),0, 0);
                        pc.sendPackets(new S_AddSkill(pc, skillId));
                        isGfx = true;
        			} else {
        				pc.sendPackets(new S_ServerMessage(189)); // \f1不足。
        			}
                }
            }
		}
		if (isGfx) {
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 224));
        }
	}

	@Override
	public String getType() {
		return C_SKILL_BUY_OK;
	}

}
