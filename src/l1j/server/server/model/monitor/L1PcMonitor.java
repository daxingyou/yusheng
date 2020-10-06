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
package l1j.server.server.model.monitor;

//import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

/**
 * L1PcInstance定期处理、监视处理等行为共通的处理实装抽像
 * 
 * 各处理{@link #run()}{@link #execTask(L1PcInstance)}实装。
 * PC上存在场合、run()即座。
 * 场合、定期实行、处理等停止必要。
 * 停止止、永远定期实行。
 * 定期实行单发场合制御不要。
 * 
 * L1PcInstance参照直接持望。
 * 
 * @author frefre
 *
 */
public abstract class L1PcMonitor implements Runnable {

	/** 对像L1PcInstanceID */
	protected L1PcInstance _pc;

	/**
	 * 指定L1PcInstance对作成。
	 * @param oId {@link L1PcInstance#getId()}取得ID
	 */
	public L1PcMonitor(L1PcInstance pc) {
		_pc = pc;
	}

	@Override
	public final void run() {
		//L1PcInstance pc = (L1PcInstance) L1World.getInstance().findObject(_id);
		if (_pc == null || _pc.getNetConnection() == null) {
			return;
		}
		execTask(_pc);
	}

	/**
	 * 实行时处理
	 * @param pc 对像PC
	 */
	public abstract void execTask(L1PcInstance pc);
}
