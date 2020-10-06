/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.serverpackets;

import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_InvList extends ServerBasePacket {
	private static final String S_INV_LIST = "[S] S_InvList";
	/**
     * 物品名单(背包)
     */
    public S_InvList(final List<L1ItemInstance> items){
        this.writeC(Opcodes.S_OPCODE_INVLIST);
        this.writeC(items.size()); // 道具数量

        for (final L1ItemInstance item : items) {
            this.writeD(item.getId());

            switch (item.getItem().getItemId()) {
                case 40318:
                    writeH(166);
                    break;
                case 40319:
                    writeH(569);
                    break;
                case 40321:
                    writeH(837);
                    break;
                case 49158:
                    writeH(3604);
                    break;
                case 49157:
                    writeH(3605);
                    break;
                case 49156:
                    writeH(3606);
                    break;
                default:
                    writeH(0);
                    break;
            }

            int type = item.getItem().getUseType();
            if (type < 0) {
                type = 0;
            }
            if (type == 96 || type >= 98) {
                this.writeC(26);
            } else if (type == 97) {
                this.writeC(27);
            } else {
                this.writeC(type);// 使用类型
            }
            if (item.getChargeCount() <= 0) {
                this.writeC(0x00);// 可用次数
            } else {
                this.writeC(item.getChargeCount());// 可用次数
            }

            this.writeH(item.get_gfxid());// 图示
            this.writeC(item.getBless());// 祝福状态

            this.writeD((int) Math.min(item.getCount(), 2000000000));// 数量
            int statusX = 0;
            if (item.isIdentified())
                statusX |= 1;
            if (!item.getItem().isTradable() && !item.isTradable())
                statusX |= 2;
            if (item.getItem().isCantDelete())
                statusX |= 4;
            if (item.getItem().get_safeenchant() < 0
                    || item.getItem().getUseType() == -3
                    || item.getItem().getUseType() == -2)
                statusX |= 8;
            if (item.getBless() >= 128) {
                statusX = 32;
                if (item.isIdentified()) {
                    statusX |= 1;
                    statusX |= 2;
                    statusX |= 4;
                    statusX |= 8;
                } else {
                    statusX |= 2;
                    statusX |= 4;
                    statusX |= 8;
                }
            }
            this.writeC(statusX);
            // this.writeC((item.isIdentified()) ? 0x01 : 0x00);// 鉴定状态
            this.writeS(item.getViewName());// 名称
            if (!item.isIdentified()) {
                // 未见定状态 不需发送详细资料
                this.writeC(0x00);
            } else {
            	byte[] status = item.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
            }
            writeC(10);
            writeH(0);
            writeD(0);
            writeD(0);
        }
    }

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_INV_LIST;
	}
}
