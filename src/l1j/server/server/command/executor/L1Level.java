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

import java.util.StringTokenizer;

import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.RangeInt;
import l1j.server.server.world.L1World;

public class L1Level implements L1CommandExecutor {

	private L1Level() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Level();
	}

	/** 目标. */
	private L1PcInstance target;

	/** 等级. */
	private int level;
	
	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
//		try {
//			StringTokenizer tok = new StringTokenizer(arg);
//			int level = Integer.parseInt(tok.nextToken());
//			if (level == pc.getLevel()) {
//				return;
//			}
//			if (!IntRange.includes(level, 1, 99)) {
//				pc.sendPackets(new S_SystemMessage("请在1-99范围内指定"));
//				return;
//			}
//			pc.setExp(ExpTable.getExpByLevel(level));
//			pc.resExp();
//		} catch (Exception e) {
//			pc.sendPackets(new S_SystemMessage(cmdName + "lv 请输入"));
//		}
//	}
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final String charName = tok.nextToken();
			if (tok.hasMoreTokens()) {
				this.level = Integer.parseInt(tok.nextToken());
				this.target = L1World.getInstance().getPlayer(charName);
			} else {
				this.level = Integer.parseInt(charName);
				this.target = pc;
			}

			if (level == this.target.getLevel()) {
				return;
			}
			if (!IntRange.includes(level, 1, 99)) {
				pc.sendPackets(new S_SystemMessage("范围限制 1~"
						+ ExpTable.MAX_LEVEL));
				return;
			}
			this.target.setExp(ExpTable.getExpByLevel(level));
			target.resExp();

		} catch (final Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + "lv 请输入"));
			// 261 \f1指令错误。
			//pc.sendPackets(new S_SystemMessage(261));
			pc.sendPackets(new S_SystemMessage(cmdName + "lv 请输入"));
		}
	}
}
