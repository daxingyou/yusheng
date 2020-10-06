package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javolution.util.FastMap;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.ShouBaoStorage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShouBaoTemp;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShouBaoTable implements ShouBaoStorage{
	private static final Log _log = LogFactory.getLog(ShouBaoTable.class);
	
	private final Map<Integer,L1ShouBaoTemp> _alltemps = new FastMap<Integer,L1ShouBaoTemp>();

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
			pstm = con.prepareStatement("SELECT * FROM server_shoubao");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int itemId = rs.getInt("itemId");
				final int gvItemId = rs.getInt("giveItemId");
				final int count = rs.getInt("giveCount");
				final int objId = rs.getInt("byObjId");
				final String name = rs.getString("byName");
				final L1Item item = ItemTable.getInstance().getTemplate(itemId);
				final L1Item giveitem = ItemTable.getInstance().getTemplate(gvItemId);
				if (item != null && giveitem != null){
					final L1ShouBaoTemp tmp = new L1ShouBaoTemp();
					tmp.setGiveItemId(gvItemId);
					tmp.setItemId(itemId);
					tmp.setGiveItemCount(count);
					tmp.setObjId(objId);
					tmp.setName(name);
					tmp.setItemName(item.getName());
					tmp.setGiveItemName(giveitem.getName());
					
					_amcount0 += count;
					
					if (objId == 0){
						_count++;
						_amcount1 += count;
					}
					_alltemps.put(itemId, tmp);
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		_log.info("載入首爆數量: " + _alltemps.size() + "(" + timer.get() + "ms)");
	}
	
	@Override
	public L1ShouBaoTemp getTemp(final int itemId){
		return _alltemps.get(itemId);
	}
	
	@Override
	public Map<Integer,L1ShouBaoTemp> getShouBaoMaps(){
		return _alltemps;
	}
	
	@Override
	public boolean update(final L1ShouBaoTemp tmp,final int objId,final String name){
		Connection con = null;
		PreparedStatement pstm = null;
		if (tmp.getObjId() != 0){
			return false;
		}
		try {
			//final Timestamp lastactive = new Timestamp(System.currentTimeMillis());
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "UPDATE `server_shoubao` SET `byName`=?,`byObjId`=? WHERE `itemId`=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, name);
			pstm.setInt(2, objId);
			pstm.setInt(3, tmp.getItemId());
			pstm.execute();
			tmp.setObjId(objId);
			tmp.setName(name);
			_count--;
			_amcount1 -= tmp.getGiveItemCount();
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
