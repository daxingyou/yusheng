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

import java.util.ArrayList;
import java.util.List;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_SkillBuy extends ServerBasePacket {
	public S_SkillBuy(int o, L1PcInstance player) {
		
		List<Integer> canSkillList = Scount(player);
		writeC(Opcodes.S_OPCODE_SKILLBUY);
		writeD(100);
		writeH(canSkillList.size());
		for (int k = 0; k < canSkillList.size(); k++) {
			writeD(canSkillList.get(k));
		}
		canSkillList.clear();
	}

	public List<Integer> Scount(L1PcInstance player) {
		int RC = 0;
		List<Integer> canSkillList = new ArrayList<Integer>();
		switch (player.getType()) {
		case 0: // 君主
			break;
		case 1: // 
			RC = 8;
			break;
		case 2: // 
			RC = 23;
			break;
		case 3: // WIZ
			RC = 23;
			break;
		case 4: // DE
			RC = 16;
			break;
		default:
			break;
		}
		for(int i = 0; i < RC;i++){
			if (CharSkillReading.get().spellCheck(player.getId(),i+1)){
				continue;
			}
			canSkillList.add(i);
		}
		return canSkillList;
	}

	/*
	 * public boolean chk(L1PcInstance player, int i) { boolean have = false;
	 * try { Connection connection = null; connection =
	 * L1DatabaseFactory.getInstance().getConnection(); PreparedStatement
	 * preparedstatement = connection.prepareStatement("SELECT * FROM
	 * character_skills WHERE char_obj_id=?"); preparedstatement.setInt(1,
	 * player.get_objectId()); ResultSet resultset =
	 * preparedstatement.executeQuery(); while (resultset.next()) { int b =
	 * resultset.getInt(3); if (i == b) { have = true; } } resultset.close();
	 * preparedstatement.close(); connection.close(); } catch (Exception
	 * exception) { } return have; }
	 */
	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return "[S] S_SkillBuy";
	}
}
