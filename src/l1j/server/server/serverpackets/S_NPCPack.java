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

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_NPCPack extends ServerBasePacket {

	private static final String _S__1F_NPCPACK = "[S] S_NPCPack";
	private static final Random _random = new Random();
	
	public S_NPCPack(L1NpcInstance npc) {
		buildPacket(npc);
	}

	private void buildPacket(L1NpcInstance npc) {
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());
		writeH(npc.getGfxId());
		if (npc.getNpcTemplate().is_doppel() && npc.getGfxId() != 31) { // 姿
			writeC(4); // 长剑
		} else {
			// 删除writeC(npc.getStatus());
			// 武器状态设定 
			if (npc.getNpcTemplate().getAttStatus() != 0) {
				writeC(npc.getNpcTemplate().getAttStatus());
			} else {
				writeC(npc.getStatus());
			}
			// 武器状态设定  end
		}
		writeC(npc.getHeading());
		writeC(npc.getLightSize());
		writeC(npc.getMoveSpeed());
		writeD(npc.getExp());
		writeH(npc.getTempLawful());
		if (Config.mobRandomName){
			if (npc instanceof L1MonsterInstance){
				writeS(String.format("%s(%d)", npc.getNameId(),_random.nextInt(9999) + 1000));
			}else{
				writeS(npc.getNameId());
			}
		}else{
			writeS(npc.getNameId());
		}
		if (npc instanceof L1FieldObjectInstance) { // SIC壁字、看板
			L1NpcTalkData talkdata = NPCTalkDataTable.getInstance()
					.getTemplate(npc.getNpcTemplate().get_npcId());
			if (talkdata != null) {
				writeS(talkdata.getNormalAction()); // HTML名解
			} else {
				writeS(null);
			}
		} else {
			writeS(npc.getTitle());
		}

		/**
		 *  - 0:mob,item(atk pointer), 1:poisoned(), 2:invisable(), 4:pc,
		 * 8:cursed(), 16:brave(), 32:??, 64:??(??), 128:invisable but name
		 */
		int bit = 0;
		/*删除if (npc.getNpcTemplate().is_doppel()) {
			// PC属性祝福渡WIZ例外
			if (npc.getNpcTemplate().get_npcId() != 81069) {
				bit |= 4;
			}
		}删除*/
		// 守城警卫须强制攻击 
		if (npc.getNpcTemplate().get_npcId() >= l1j.william.New_Id.Npc_AJ_4_1 && 
			npc.getNpcTemplate().get_npcId() <= l1j.william.New_Id.Npc_AJ_4_28) {
			bit |= 4;
		}
		// 守城警卫须强制攻击  end
		writeC(bit);

		writeD(0); // 0以外C_27飞
		writeS(null);
		writeS(null); // 名？
		writeC(0);
		writeC(0xFF); // HP
		writeC(0);
		writeC(npc.getLevel());
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
		/*
		 * writeC(68); writeH(npc.get_x()); writeH(npc.get_y());
		 * writeD(npc.get_objectId()); writeH(npc.get_gfxid());
		 * writeC(npc.get_status()); writeC(npc.get_heading());
		 * writeC(npc.get_NPCType()); writeC(i); writeC(j); writeH(0);
		 * writeC(0); writeH(npc.get_lawful()); writeS(npc.get_nameid());
		 * writeC(0); writeD(0); writeD(0); writeC(255); writeC(0);
		 * writeC(npc.get_level()); writeC(0); writeH(65535); //
		 * writeD(0x401799a); //GameTime?
		 * writeD(GameTimeController.getInstance().getGameTime()); //GameTime反定
		 * writeC(8); writeC(0);
		 */
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__1F_NPCPACK;
	}

}
