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
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_HireSoldier extends ClientBasePacket {

	private static final String C_HIRE_SOLDIER = "[C] C_HireSoldier";



	// S_HireSoldier送表示雇用OK押送
	public C_HireSoldier(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		int something1 = readH(); // S_HireSoldier引数
		int something2 = readH(); // S_HireSoldier引数
		int something3 = readD(); // 1以外入？
		int something4 = readD(); // S_HireSoldier引数
		int number = readH(); // 雇用数
		
		// < 佣兵雇用处理
	}

	@Override
	public String getType() {
		return C_HIRE_SOLDIER;
	}
}
