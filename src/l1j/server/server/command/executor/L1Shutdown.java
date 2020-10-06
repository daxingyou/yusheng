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

import java.util.logging.Logger;
import l1j.server.server.Shutdown;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Shutdown implements L1CommandExecutor {

	private L1Shutdown() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Shutdown();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			if (arg.equalsIgnoreCase("now")) {
				Shutdown.getInstance().startShutdown(pc, 5, true);
				return;
			}
			if (arg.equalsIgnoreCase("abort")) {
				Shutdown.getInstance().abort(pc);
				return;
			}
			int sec = Math.max(5, Integer.parseInt(arg));
			Shutdown.getInstance().startShutdown(pc, sec, true);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".shutdown sec|now|abort 请输入。"));
		}
	}
}
