package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class FindItemCountTable {
	private static final Log _log = LogFactory.getLog(FindItemCountTable.class);

	private static final ArrayList<L1FindItemCountTemp> _itemIdList = new ArrayList<L1FindItemCountTemp>();
	
	private static FindItemCountTable _instance;
	
	private String[] itemData = null;
	private String[] ArmorData = null;
	private String[] WeaponData = null;


	private class L1FindItemCountTemp{
		public int _itemId;
		public String _itemName;
	}
	public static FindItemCountTable get() {
		if (_instance == null) {
			_instance = new FindItemCountTable();
		}
		return _instance;
	}
	
	private class UpdateObjectCTimer extends TimerTask {

	    private ScheduledFuture<?> _timer;

	    public void start() {
	        final int timeMillis = 3*60*1000;// 3分钟
	        _timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
	                timeMillis);
	    }

	    @Override
	    public void run() {
	        try {
	        	itemData = FindItemCountTable.get().getItemDataTime();
	    		ArmorData = FindItemCountTable.get().getArmorDataTime();
	    		WeaponData = FindItemCountTable.get().getWeaponDataTime();
	        } catch (final Exception e) {
	        	GeneralThreadPool.getInstance().cancel(_timer, false);
	            final UpdateObjectCTimer objectCTimer = new UpdateObjectCTimer();
	            objectCTimer.start();
	        }
	    }
	}


	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `可查询总数的道具` ORDER BY `id`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final String item_name = rs.getString("name");
				final int item_id = rs.getInt("itemId");
				final L1FindItemCountTemp data = new L1FindItemCountTemp();
				data._itemId = item_id;
				data._itemName = item_name;
				_itemIdList.add(data);
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
		itemData = this.getItemDataTime();
		ArmorData = this.getArmorDataTime();
		WeaponData = this.getWeaponDataTime();
		
		final UpdateObjectCTimer objectCTimer = new UpdateObjectCTimer();
        objectCTimer.start();
        
        _log.info("載入可查询总数的道具: " + _itemIdList.size() + "(" + timer.get() + "ms)");
	}
	public String[] getItemData(){
		return itemData;
	}
	public String[] getArmorData(){
		return ArmorData;
	}
	public String[] getWeaponData(){
		return WeaponData;
	}
	public String[] getItemDataTime(){
		Connection con = null;
		final String[] data = new String[_itemIdList.size()];
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement pstm = null;
			ResultSet rs = null;
			for(int i = 0;i<_itemIdList.size();i++){
				try {
					pstm = con.prepareStatement("SELECT SUM(count) as counts FROM character_items,characters WHERE characters.objid = character_items.char_id AND characters.AccessLevel = 0 AND item_id=?");
					pstm.setInt(1, _itemIdList.get(i)._itemId);
					rs = pstm.executeQuery();
					final StringBuilder msg = new StringBuilder(_itemIdList.get(i)._itemName);
					msg.append(":");
					if (!rs.next()){
						msg.append("0");
					}else{
						String c = rs.getString("counts");//String.valueOf(rs.getLong("counts"));
						if (c == null || c.equals("0")){
							msg.append("0");
						}else{
							if (c.length() > 4){
								c = c.substring(0, c.length() - 4) + " W";
							}
							msg.append(c);
						}
					}
					data[i] = msg.toString();
				} catch (final SQLException e) {
					_log.error(e.getLocalizedMessage(), e);
				} finally {
					SQLUtil.close(rs);
					SQLUtil.close(pstm);
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(con);
		}
		return data;
	}
	
	public String[] getWeaponDataTime(){
		final String[] data = new String[7];
		data[0]="武器";
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement pstm = null;
			ResultSet rs = null;
			for(int i = 0;i<6;i++){
				try {
					pstm = con.prepareStatement("SELECT SUM(count) as counts FROM character_items,weapon,characters WHERE characters.objid = character_items.char_id AND characters.AccessLevel = 0 AND character_items.item_id = weapon.item_id AND character_items.enchantlvl > 0 AND character_items.enchantlvl - weapon.safenchant >= ?");
					pstm.setInt(1, i+1);
					rs = pstm.executeQuery();
					final StringBuilder msg = new StringBuilder();
					if (!rs.next()){
						msg.append("0");
					}else{
						String c = rs.getString("counts");//String.valueOf(rs.getLong("counts"));
						if (c == null || c.equals("0")){
							msg.append("0");
						}else{
							msg.append(c);
						}
					}
					data[i+1] = msg.toString();
				} catch (final SQLException e) {
					_log.error(e.getLocalizedMessage(), e);
				} finally {
					SQLUtil.close(rs);
					SQLUtil.close(pstm);
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(con);
		}
		return data;
	}
	public String[] getArmorDataTime(){
		final String[] data = new String[7];
		data[0]="防具";
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement pstm = null;
			ResultSet rs = null;
			for(int i = 0;i<6;i++){
				try {
					pstm = con.prepareStatement("SELECT SUM(count) as counts FROM character_items,armor,characters WHERE characters.objid = character_items.char_id AND characters.AccessLevel = 0 AND character_items.item_id = armor.item_id  AND character_items.enchantlvl > 0 AND character_items.enchantlvl - armor.safenchant >= ?");
					pstm.setInt(1, i+1);
					rs = pstm.executeQuery();
					final StringBuilder msg = new StringBuilder();
					if (!rs.next()){
						msg.append("0");
					}else{
						String c = rs.getString("counts");//String.valueOf(rs.getLong("counts"));
						if (c == null || c.equals("0")){
							msg.append("0");
						}else{
							msg.append(c);
						}
					}
					data[i+1] = msg.toString();
				} catch (final SQLException e) {
					_log.error(e.getLocalizedMessage(), e);
				} finally {
					SQLUtil.close(rs);
					SQLUtil.close(pstm);
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(con);
		}
		return data;
	}
}
