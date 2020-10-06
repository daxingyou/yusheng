package l1j.server.data;


import org.apache.commons.logging.Log;


/**
 * 错误讯息
 * @author daien
 *
 */
public class DataError {

	private static boolean _debug = false;
	/**
	 * 错误讯息
	 * @param log
	 * @param string
	 * @param e
	 */
	public static void isError(final Log log, String string, Exception e) {
		if (_debug) {
			log.error(string, e);
		}
	}
}
