package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChatObsceneTable {
	private static final Log _log = LogFactory.getLog(ChatObsceneTable.class);
	private final HashMap<String, String> obsceneMaps = new HashMap<String, String>();
	
	private static ChatObsceneTable _instance;

	private ChatObsceneTable() {
	}

	public static ChatObsceneTable getInstance() {
		if (_instance == null) {
			_instance = new ChatObsceneTable();
		}
		return _instance;
	}
	
	public void reload(){
		obsceneMaps.clear();
		load();
	}
	
	public void load(){
		PerformanceTimer timer = new PerformanceTimer();
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM server_obscene");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final String checkText = rs.getString("obscene");
				if (checkText.isEmpty() || checkText.length() <= 0){
					continue;
				}
				obsceneMaps.put(checkText, getParamText(checkText));
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		_log.info("載入server_obscene數量: " + obsceneMaps.size() + "(" + timer.get() + "ms)");
	}
	
	public String getObsceneText(final String checkText){
		String text = checkText;
		for(final Entry<String, String> iter : obsceneMaps.entrySet()){
			text = text.replace(iter.getKey(), iter.getValue());
		}
		return text;
	}

	private String getParamText(final String checkText) {
		final StringBuilder paramBuilder = new StringBuilder();
		for(int i = 0; i < checkText.length();i++){
			paramBuilder.append("*");
		}
		return paramBuilder.toString();
	}

}
