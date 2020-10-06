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
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateParty extends ClientBasePacket {

	private static final String C_CREATE_PARTY = "[C] C_CreateParty";

	public C_CreateParty(byte decrypt[], LineageClient _client) throws Exception {
		super(decrypt);
        final L1PcInstance pc = _client.getActiveChar();
        if (pc.isGhost()) { // 鬼魂模式
            return;
        }
        if (pc.isDead()) { // 死亡
            return;
        }
        if (pc.isTeleport()) { // 传送中
            return;
        }
        int targetId;
        L1Object temp;
        final int type = this.readC();

        switch (type) {
            case 0:
            case 1: // パーティー(パーティー自动分配ON/OFFで异なる)
                targetId = this.readD();
                temp = L1World.getInstance().findObject(targetId);
                if (temp instanceof L1PcInstance) {
                    final L1PcInstance targetPc = (L1PcInstance) temp;
                    if (pc.getId() == targetPc.getId()) {
                        return;
                    }
                    if (targetPc.isInParty()) {
                        // 您无法邀请已经参加其他队伍的人。
                        pc.sendPackets(new S_ServerMessage(415));
                        return;
                    }
                    if (pc.isInParty()) {
                        if (pc.getParty().isLeader(pc)) {
                            targetPc.setPartyID(pc.getId());
                            // 玩家 %0%s 邀请您加入队伍？(Y/N)
                            targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
                        } else {
                            // 只有领导者才能邀请其他的成员。
                            pc.sendPackets(new S_ServerMessage(416));
                        }

                    } else {
                        targetPc.setPartyID(pc.getId());
                        // 玩家 %0%s 邀请您加入队伍？(Y/N)
                        targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
                    }
                }
                break;

            case 2: // チャットパーティー
                final String name = this.readS();
                final L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
                if (targetPc == null) {
                	// 没有叫%0的人。
                    pc.sendPackets(new S_ServerMessage(109));
                    return;
                }
                if (pc.getId() == targetPc.getId()) {
                    return;
                }
                break;
            case 3:// 队长转移
            	targetId = readD();
    			temp = L1World.getInstance().findObject(targetId);
    			if (temp instanceof L1PcInstance) {
    				L1PcInstance target_pc = (L1PcInstance) temp;
    				if (pc.getId() == target_pc.getId()) {
    					return;
    				}
    				if (pc.isInParty()) {
    					if (target_pc.isInParty()) {
    						if (pc.getParty().isLeader(pc)) {
    							if (pc.getLocation().getTileLineDistance(target_pc.getLocation()) < 16) {
    								//pc.getParty().passLeader(target_pc);
    							} else {
    								pc.sendPackets(new S_ServerMessage(1695));
    							}
    						} else {
    							pc.sendPackets(new S_ServerMessage(1697));
    						}
    					} else {
    							pc.sendPackets(new S_ServerMessage(1696));
    					}
    				}
    			}
    			break;
        }
	}

	@Override
	public String getType() {
		return C_CREATE_PARTY;
	}

}
