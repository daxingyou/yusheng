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
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_ChatPacket extends ServerBasePacket {


	private static final String _S__1F_NORMALCHATPACK = "[S] S_ChatPacket";


	public S_ChatPacket(L1PcInstance pc, String chat, int opcode, int type) {

		if (type == 0) // 通常
		{
			writeC(opcode);
			writeC(type);
			if (pc.isInvisble()) {
				writeD(0);
			} else {
				writeD(pc.getId());
			}
			writeS(pc.getName() + ": " + chat);
		} else if (type == 2) // 叫
		{
			writeC(opcode);
			writeC(type);
			if (pc.isInvisble()) {
				writeD(0);
			} else {
				writeD(pc.getId());
			}
			writeS("<" + pc.getName() + "> " + chat);
			writeH(pc.getX());
			writeH(pc.getY());
		} else if (type == 3) // 全体
		{
			writeC(opcode);
			writeC(type);
			if (pc.isGm() == true) {
				writeS("[******] " + chat);
			} else {
				writeS("[" + pc.getName() + "] " + chat);
			}
		} else if (type == 4) // 血盟
		{
			writeC(opcode);
			writeC(type);
			writeS("{" + pc.getName() + "} " + chat);
		} else if (type == 9) // 
		{
			writeC(opcode);
			writeC(type);
			writeS("-> (" + pc.getName() + ") " + chat);
		} else if (type == 11) // 
		{
			writeC(opcode);
			writeC(type);
			writeS("(" + pc.getName() + ") " + chat);
		}else if (type == 12) {
			writeC(opcode);
			writeC(type);
			if (pc.isGm() == true) {
				writeS("[******] " + chat);
			} else {
				writeS("[" + pc.getName() + "] " + chat);
			}			
		} else if (type == 13) // 连合
		{
			writeC(opcode);
			writeC(4);
			writeS("{{" + pc.getName() + "}} " + chat);
		} else if (type == 14) // 
		{
			writeC(opcode);
			writeC(type);
			writeS("(" + pc.getName() + ") " + chat);
		} else if (type == 16) // 
		{
			writeC(opcode);
			writeS(pc.getName());
			writeS(chat);
		}
	}
	
	public S_ChatPacket(L1Character mob, String chat,int opcode) {
		writeC(opcode);
		writeC(0);
		writeD(mob.getId());
		writeS(chat);
	}
	public S_ChatPacket(L1Character mob, String chat) {
		writeC(Opcodes.S_OPCODE_NORMALCHAT);
		writeC(2);
		writeD(mob.getId());
		writeS("<" + mob.getName() + "> " + chat);
		writeH(mob.getX());
		writeH(mob.getY());
	}
	
	  public S_ChatPacket(L1Object object, String chat, int x) {
		    writeC(Opcodes.S_OPCODE_NORMALCHAT);
		    writeC(0);
		    writeD(object.getId());
		    writeS(chat);
		  }
	  
	  public S_ChatPacket(L1NpcInstance npc, String chat, int opcode, int type) {

			if (type == 0) // 通常
			{
				writeC(opcode);
				writeC(type);
				if (npc.isInvisble()) {
					writeD(0);
				} else {
					writeD(npc.getId());
				}
				writeS(npc.getName() + ": " + chat);
			} else if (type == 2) // 叫
			{
				writeC(opcode);
				writeC(type);
				if (npc.isInvisble()) {
					writeD(0);
				} else {
					writeD(npc.getId());
				}
				writeS("<" + npc.getName() + "> " + chat);
				writeH(npc.getX());
				writeH(npc.getY());
			} else if (type == 3) // 全体
			{
				writeC(opcode);
				writeC(type);
				writeS("[" + npc.getName() + "] " + chat);
			} 
		}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__1F_NORMALCHATPACK;
	}

}