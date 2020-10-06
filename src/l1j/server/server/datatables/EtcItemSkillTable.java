package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EtcItemSkillTable {
	private static final Log _log = LogFactory.getLog(EtcItemSkillTable.class);
	private final Map<Integer, int[]> _ItemSkillMap = new HashMap<Integer, int[]>();
	private final ArrayList<Integer> _skillIds = new ArrayList<Integer>();
	private static EtcItemSkillTable _instance;

	public static EtcItemSkillTable getInstance() {
		if (_instance == null) {
			_instance = new EtcItemSkillTable();
		}
		return _instance;
	}

	private EtcItemSkillTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM etcitem_skill");

			rs = pstm.executeQuery();
			while (rs.next()) {
				final int[] skillArray = new int[19];
				final int skillId = rs.getInt("skillId");
				skillArray[0] = rs.getInt("time");
				skillArray[1] = rs.getInt("gfxid");
				skillArray[2] = rs.getInt("addstr");
				skillArray[3] = rs.getInt("adddex");
				skillArray[4] = rs.getInt("addcon");
				skillArray[5] = rs.getInt("addint");
				skillArray[6] = rs.getInt("addwis");
				skillArray[7] = rs.getInt("addcha");
				skillArray[8] = rs.getInt("addhp");
				skillArray[9] = rs.getInt("addmp");
				skillArray[10] = rs.getInt("addhpr");
				skillArray[11] = rs.getInt("addmpr");
				skillArray[12] = rs.getInt("addsp");
				skillArray[13] = rs.getInt("addmr");
				skillArray[14] = rs.getInt("addac");
				skillArray[15] = rs.getInt("addhit");
				skillArray[16] = rs.getInt("adddmg");
				skillArray[17] = rs.getInt("addexp");
				skillArray[18] = rs.getInt("adder");
				
				_skillIds.add(skillId);
				_ItemSkillMap.put(skillId, skillArray);
			}

		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入etcitem_skill數量: " + _ItemSkillMap.size() + "(" + timer.get() + "ms)");
	}
	
	public int[] getEtcitemSkillArray(final int skillId){
		return _ItemSkillMap.get(skillId);
	}
	
	public ArrayList<Integer> getSkillList(){
		return _skillIds;
	}
}
