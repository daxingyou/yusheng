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
/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/Server.java,v 1.5 2004/11/19 08:54:43 l2chef Exp $
 *
 * $Author: l2chef $
 * $Date: 2004/11/19 08:54:43 $
 * $Revision: 1.5 $
 * $Log: Server.java,v $
 * Revision 1.5  2004/11/19 08:54:43  l2chef
 * database is now used
 *
 * Revision 1.4  2004/07/08 22:42:28  l2chef
 * logfolder is created automatically
 *
 * Revision 1.3  2004/06/30 21:51:33  l2chef
 * using jdk logger instead of println
 *
 * Revision 1.2  2004/06/27 08:12:59  jeichhorn
 * Added copyright notice
 */
package l1j.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogManager;

import l1j.gui.J_Main;
import l1j.server.server.GameServer;
import l1j.server.telnet.TelnetServer;

import org.apache.log4j.PropertyConfigurator;

/**
 * l1j-jp起动.
 */
public class Server {
	
	/** 设定. */
	private static final String LOG_PROP = "./config/log.properties";
	
	
	
	private static final String _log_prop = "./config/logging.properties";

	private static final String _log_4j = "./config/log4j.properties";

	private static final String _loginfo = "./loginfo";
	
	private static final String _back = "./back";

	/**
	 * .
	 *
	 * @param args
	 *            引数
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
/*		File logFolder = new File("log");
		logFolder.mkdir();

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			_log.log(Level.SEVERE, "Failed to Load " + LOG_PROP + " File.", e);
			System.exit(0);
		}*/
		// 压缩旧档案
		final CompressFile bean = new CompressFile();
		try {
			// 建立备份用资料夹
			final File file = new File(_back);
			if (!file.exists()) {
				file.mkdir();
			}

			final String nowDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
					.format(new Date());
			bean.zip(_loginfo, "./back/" + nowDate + ".zip");

			final File loginfofile = new File(_loginfo);
			final String[] loginfofileList = loginfofile.list();
			for (final String fileName : loginfofileList) {
				final File readfile = new File(_loginfo + "/" + fileName);
				if (readfile.exists() && !readfile.isDirectory()) {
					readfile.delete();
				}
			}

		} catch (final IOException e) {
			System.out.println("资料夹不存在: " + _back + " 已经自动建立!");
		}
		
		try {
			final InputStream is = new BufferedInputStream(new FileInputStream(
					_log_prop));
			LogManager.getLogManager().readConfiguration(is);
			is.close();

		} catch (final IOException e) {
			System.out.println("档案遗失: " + _log_prop);
		}

		try {
			PropertyConfigurator.configure(_log_4j);

		} catch (final Exception e) {
			System.out.println("档案遗失: " + _log_4j);
			System.exit(0);
		}
		
		Config.load();

		// L1DatabaseFactory初期设定
		L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL,
				Config.DB_LOGIN, Config.DB_PASSWORD);
		L1DatabaseFactory.getInstance();
		J_Main.getInstance().setVisible(true);
		GameServer.getInstance().initialize();
		if (Config.TELNET_SERVER) {
			TelnetServer.getInstance().start();
		}
	}
}
