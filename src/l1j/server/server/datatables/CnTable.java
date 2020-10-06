package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.CnStorage;
import l1j.server.server.templates.Tbs;
import l1j.server.server.utils.SQLUtil;

/**
 * Cn纪录资料
 * @author dexc
 *
 */
public class CnTable implements CnStorage {

	private static final Log _log = LogFactory.getLog(CnTable.class);

	private static final Map<Integer, Tbs> _vipMap =
		new ConcurrentHashMap<Integer, Tbs>();

	private static final Map<String, Tbs> _tbsMap =
			new ConcurrentHashMap<String, Tbs>();
	
	private static CnTable _instance;
	
	public static CnTable get() {
		if (_instance == null) {
			_instance = new CnTable();
		}
		return _instance;
	}
	
	private  boolean _isload = false;
	@Override
	public boolean isload(){
		return _isload;
	}
	/**
	 * 初始化载入
	 */
	@Override
	public void load() {
		_isload = true;	
		_vipMap.clear();
		_tbsMap.clear();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_cns`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int id =  rs.getInt("tbsid");
				final int isuse = rs.getInt("isuse");
				final String name = rs.getString("account_name").toLowerCase();
				final int itemid = rs.getInt("item_id");
				int count = rs.getInt("count");
				if (isuse == 0) {
					if (getCnOther(id)==null) {
					
						Tbs tbs = new Tbs();
						tbs.setTbsId(id);
						tbs.setTbsName(name);
						tbs.setCnitemid(itemid);
						tbs.setCnCount(count);
						tbs.setCnUse(false);
						addMap(id, tbs);
						if (getCnOther(name)!=null) {
						
							final Tbs ts = getCnOther(name);
							count += ts.getCnCount();
							ts.setCnCount(count);
							addcnMap(name,ts);
						}else {
							addcnMap(name,tbs);	
						}
						
					}
					
				}
			}
		} catch (final SQLException e) {
			_log.info(e.getLocalizedMessage());

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
			_isload = false;
			//_log.info("充值记录读取完成");
		}
		//_log.info("此次载入商城资料数量: " + _vipMap.size() + "(" + timer.get() + "ms)");
	}



	/**
	 * 加入纪录清单
	 * @param objId
	 * @param buffTmp
	 */
	@Override
	public  void addMap(final Integer objId, final Tbs overtime) {
		final Tbs otherTmp = _vipMap.get(objId);
		if (otherTmp == null) {
			_vipMap.put(objId, overtime);
		}
	}
	
	/**
	 * 加入纪录清单
	 * @param objId
	 * @param buffTmp
	 */
	@Override
	public  void addcnMap(final String objId, final Tbs tbs) {
			_tbsMap.put(objId, tbs);	
	}
	
	/**
	 * 加入纪录清单
	 * @param objId
	 * @param buffTmp
	 */
	@Override
	public  Tbs getCn(final String objId) {
		for (Tbs tbs : _vipMap.values()) {
			if (tbs.getTbsName() == objId) {
				return tbs;
			}
		}
		return null;			
	}
	/**
	 * 全部Cn纪录
	 * @return
	 */
	@Override
	public Map<Integer, Tbs> cnmap() {
		return _vipMap;
	}
	public Tbs getCnOther(final int key) {
		final Tbs otherTmp = _vipMap.get(key);
		return otherTmp;
	}
	/**
	 * 取回Cn系统纪录
	 * @param pc
	 */
	@Override
	public Tbs getCnOther(final String name) {
		final Tbs otherTmp = _tbsMap.get(name);
		return otherTmp;
	}

	/**
	 * 增加/更新 Cn系统纪录
	 * @param key
	 * @param value
	 */
	@Override
	public void storeCnOther(final String name) {
		for (Tbs tbs : _vipMap.values()) {
			if (tbs==null) {
				continue;
			}
			if (tbs.getTbsName().equals(name)) {
				final long timeNow = System.currentTimeMillis();// 目前时间豪秒
				final Timestamp value = new Timestamp(timeNow);
				tbs.setCnUse(true);
				tbs.setDate(value);
				delCnOther(tbs);
				
			}
			
		}

/*		for (int i = 0; i < _vipMap.size(); i++) {
			Tbs tbs = _vipMap.get(i);
			if (tbs==null) {
				continue;
			}
			_log.info("清单遍历"+i+"时帐号为"+tbs.getTbsName());
			if (tbs.getTbsName().equals(name)) {
				_log.info("在清单里发现帐号"+name+"");
				final long timeNow = System.currentTimeMillis();// 目前时间豪秒
				final Timestamp value = new Timestamp(timeNow);
				tbs.setCnUse(true);
				tbs.setDate(value);
				_log.info("帐号"+name+"参数设置完毕，准备更新数据库");
				delCnOther(tbs);
				
			}
		}*/
	}
	
	/**
	 * 删除Cn系统纪录
	 * @param key PC OBJID
	 */
	@Override
	public void delCnOther(final Tbs key) {
		//if (_vipMap.remove(key) != null) {
			updateOther(key);
		//}
	}
	/**
	 * 删除Cn系统纪录资料
	 * @param objid
	 */
/*	private static void delete(final int objid) {
		// 清空资料库纪录
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"DELETE FROM `character_cns` WHERE `tbsid`=?");
			ps.setInt(1, objid);
			ps.execute();

			_vipMap.remove(objid);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);

		}
	}*/
	/**
	 * 更新Cn系统纪录
	 * @param objId
	 * @param other
	 */
	private  void updateOther(final Tbs other) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement(
					"UPDATE `character_cns` SET `isuse`=?,`overtime`=? WHERE `tbsid`=?");
			
			int i = 0;			
			ps.setInt(++i, 1);
			ps.setTimestamp(++i, other.getDate());
			ps.setInt(++i, other.getTbsId());

			ps.execute();
			_tbsMap.remove(other.getTbsName());
		} catch (final SQLException e) {
			_log.info(e.getLocalizedMessage());

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 增加Cn系统纪录
	 * @param objId
	 * @param other
	 *//*
	private static void addNewOther(final int objId, final Timestamp other) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"INSERT INTO `character_cns` SET `accountname`=?,`overtime`=?");
			
			int i = 0;
			ps.setInt(++i, objId);
			ps.setTimestamp(++i, other);

			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}*/
}
