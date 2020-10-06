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
package l1j.server.server.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.logging.Logger;

import l1j.server.server.Account;

public class SystemUtil {

	/**
	 * 利用中单位返。<br>
	 * 值含。
	 * 
	 * @return 利用中
	 */
	public static long getUsedMemoryMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory()) / 1024L / 1024L;
	}
	
	/**
	 * Prings memory usage both for heap and non-heap memory.
	 */
	public static void printMemoryUsage(final Logger log) {
		// 返回的内存使用量中已使用内存量为活动对象和尚未回收的垃圾对象（如果有）所占用内存的总量。
		final MemoryUsage hm = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		// 返回 Java 虚拟机使用的非堆内存的当前使用量。
		final MemoryUsage nhm = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

		final String s1 = (hm.getUsed() / 1048576) + "/" + (hm.getMax() / 1048576) + "mb";
		final String s2 = (nhm.getUsed() / 1048576) + "/" + (nhm.getMax() / 1048576) + "mb";

		if (log != null) {
			log.info("已分配內存使用量: " + s1
					+ "\n\r信息：非分配內存使用量: " + s2);
			//log.info("非分配內存使用量: " + s2);
		}
	}
}
