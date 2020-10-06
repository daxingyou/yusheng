package l1j.server.server;

//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import java.io.FileWriter;

public class WriteLogTxt {

	private static final Log _log = LogFactory.getLog(WriteLogTxt.class);


	public static void Recording(final String name, final String info) {
		try {

			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "AllLog/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name
					+ date + ".txt", true);
			fos.write((info + " 时间："
					+ new Timestamp(System.currentTimeMillis()) + "\r\n")
					.getBytes());
			fos.close();
			/*
			 * BufferedWriter out = new BufferedWriter(new FileWriter(
			 * "AllLog/"+name+date+".txt", true)); out.write(info+" 時間："+ new
			 * Timestamp(System.currentTimeMillis()) + "\r\n"); out.close();
			 */
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
