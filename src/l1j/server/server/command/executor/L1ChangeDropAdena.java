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
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class L1ChangeDropAdena implements L1CommandExecutor {

	private L1ChangeDropAdena() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeDropAdena();
	}

	/**   线上调整金币倍率
	 *
	 * @author  by0968026609
	 */

	private Thread changeExpThread = null;

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int index = Integer.parseInt(st.nextToken());

			String arg1 = null;
			String arg2 = null;

			if (index >= 0) {
				StringTokenizer token = new StringTokenizer(arg, " ");

				if (token.hasMoreTokens()) {
					arg1 = token.nextToken().trim();
				}
				if (token.hasMoreTokens()) {
					arg2 = token.nextToken().trim();
				}
			}

			if (arg1 != null && !arg1.equals("")) {
				final double DROPADENA = Double.parseDouble(arg1);
				final double olDROPADENA = Config.RATE_DROP_ADENA;
				if (arg2 != null && !arg2.equals("")) {
					final int interval = Integer.parseInt(arg2) * 60 * 1000;
					if (this.changeExpThread == null) {
						this.changeExpThread = new Thread() {
							@Override
							public void run() {
								Config.RATE_DROP_ADENA = DROPADENA;
								try {
									sleep(interval);
								} catch (Exception e) {
									System.out.println(e);
								}
								Config.RATE_DROP_ADENA = olDROPADENA;
							}
						};
						this.changeExpThread.start();
					} else {
						this.changeExpThread.interrupt();

						this.changeExpThread = new Thread() {
							@Override
							public void run() {
								Config.RATE_DROP_ADENA = DROPADENA;
								try {
									sleep(interval);
								} catch (Exception e) {
									System.out.println(e);
								}
								Config.RATE_DROP_ADENA = olDROPADENA;
							}
						};
						this.changeExpThread.start();
					}
				} else {
					Config.RATE_DROP_ADENA = DROPADENA;
				}
				try {
					Thread.sleep(400);
				} catch (Exception e) {
					System.out.println(e);
				}
				L1World.getInstance().broadcastServerMessage("系统公告" + ": " + "金币倍率已调整为" + "【 2 】" + "倍" + "。");
				pc.sendPackets(new S_SystemMessage("金币倍率已调整为" + "【 2 】" + "倍" + "。"));
			} else {
				pc.sendPackets(new S_SystemMessage("目前的金币倍率为" + "【 2 】" + "倍" + "。"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("请输入‘.金币倍率’。"));
		}
	}
}
