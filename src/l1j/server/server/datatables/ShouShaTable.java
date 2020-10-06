package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javolution.util.FastMap;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.ShouShaStorage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1ShouShaTemp;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShouShaTable implements ShouShaStorage{
	private static final Log _log = LogFactory.getLog(ShouShaTable.class);

	private final Map<Integer,L1ShouShaTemp> _alltemps = new FastMap<Integer,L1ShouShaTemp>();

	private int _count = 0;
	private long _amcount0 = 0;
	private long _amcount1 = 0;
	@Override
	public void load(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM server_shousha");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int npcId = rs.getInt("npcId");
				final int itemId = rs.getInt("itemId");
				final int count = rs.getInt("count");
				final int objId = rs.getInt("byObjId");
				final String name = rs.getString("byName");
				
				final L1Npc npc = NpcTable.getInstance().getTemplate(npcId);
				final L1Item item = ItemTable.getInstance().getTemplate(itemId);
				if (npc != null && item != null){
					final L1ShouShaTemp tmp = new L1ShouShaTemp();
					tmp.setNpcId(npcId);
					tmp.setItemId(itemId);
					tmp.setCount(count);
					tmp.setObjId(objId);
					tmp.setName(name);
					tmp.setNpcName(npc.get_name());
					tmp.setGiveItemName(item.getName());
					
					_amcount0 += count;
					
					if (objId == 0){
						_count++;
						_amcount1 += count;
					}
					_alltemps.put(npcId, tmp);
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		_log.info("載入首杀數量: " + _alltemps.size() + "(" + timer.get() + "ms)");
	}
	
	@Override
	public L1ShouShaTemp getTemp(final int npcId){
		return _alltemps.get(npcId);
	}
	
	@Override
	public boolean update(final L1ShouShaTemp tmp,final int objId,final String name){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			//final Timestamp lastactive = new Timestamp(System.currentTimeMillis());
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "UPDATE `server_shousha` SET `byName`=?,`byObjId`=? WHERE `npcId`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, name);
			pstm.setInt(2, objId);
			pstm.setInt(3, tmp.getNpcId());
			pstm.execute();
			
			tmp.setObjId(objId);
			tmp.setName(name);
			
			_count--;
			_amcount1 -= tmp.getCount();
			
			return true;
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

	@Override
	public Map<Integer, L1ShouShaTemp> getShouShaMaps() {
		return _alltemps;
	}

	@Override
	public int getCount(){
		return _count;
	}
	
	@Override
	public long getAmcount0(){
		return _amcount0;
	}
	
	@Override
	public long getAmcount1(){
		return _amcount1;
	}
}
