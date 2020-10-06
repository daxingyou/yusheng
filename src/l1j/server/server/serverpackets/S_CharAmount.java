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

public class S_CharAmount extends ServerBasePacket {

	private byte[] _byte = null;

    /**
     * 角色列表
     * 
     * @param value
     *            已创人物数量
     * @param client
     */
    public S_CharAmount(final int value) {
        this.buildPacket(value);
    }

    private void buildPacket(final int value) {
        final int maxAmount = 4 + value;
        // 0000: 0c 04 06 81 00 90 01 a3 ........
        this.writeC(Opcodes.S_OPCODE_CHARAMOUNT);
        this.writeC(value);
        this.writeC(maxAmount); // max amount
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
