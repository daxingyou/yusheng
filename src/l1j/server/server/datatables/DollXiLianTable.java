package l1j.server.server.datatables;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.doll.L1DollExecutor;
import l1j.server.server.templates.L1DollItem;
import l1j.server.server.utils.SQLUtil;

public class DollXiLianTable {
	private static final Log _log = LogFactory.getLog(DollXiLianTable.class);
	private final Map<Integer, ArrayList<L1DollExecutor>> _alldolls = new HashMap<Integer, ArrayList<L1DollExecutor>>();
	private final Map<String,L1DollItem> _alldollPowers = new HashMap<String, L1DollItem>();
	private static DollXiLianTable _instance;
	
	private DollXiLianTable(){
		
	}
	public static DollXiLianTable get(){
		if (_instance == null){
			_instance = new DollXiLianTable();
		}
		return _instance;
	}
	
	public void load(){
		
		loadDollPower();
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM items_dolls");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int objId = rs.getInt("item_objId");
				final String className = rs.getString("dollClassname");
				final int value1 = rs.getInt("value1");
				final int value2 = rs.getInt("value2");
				
				final L1DollItem dollItem = _alldollPowers.get(className);
				if (dollItem == null){
					continue;
				}
				final L1DollExecutor exe = getDollType(className, value1, value2,dollItem.get_name(),dollItem.get_nameId());
				if (exe != null){
					ArrayList<L1DollExecutor> lists = _alldolls.get(objId);
					if (lists == null){
						lists = new ArrayList<L1DollExecutor>();
						_alldolls.put(objId, lists);
					}
					lists.add(exe);
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("items_dolls数量: " + _alldolls.size());
	}
	
	private void loadDollPower(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM etictem_doll_xilian");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final String className = rs.getString("classname");
				final int minvalue1 = rs.getInt("minValue1");
				final int maxvalue1 = rs.getInt("maxValue1");
				final int minvalue2 = rs.getInt("minValue2");
				final int maxvalue2 = rs.getInt("maxValue2");


				final String name = rs.getString("name");
				final String nameId = rs.getString("nameId");
				
				final L1DollItem dollItem = new L1DollItem();
				dollItem.set_classname(className);
				dollItem.set_minValue1(minvalue1);
				dollItem.set_maxValue1(maxvalue1);
				dollItem.set_minValue2(minvalue2);
				dollItem.set_maxValue2(maxvalue2);

				dollItem.set_name(name);
				dollItem.set_nameId(nameId);
				
				_alldollPowers.put(className, dollItem);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public ArrayList<L1DollExecutor> getDollTypes(final int itemobjId){
		return _alldolls.get(itemobjId);
	}
	
	public Collection<L1DollItem> getDollPowers(){
		return _alldollPowers.values();
	}
	
	public void clearDollTypes(final int itemobjId) {
		final ArrayList<L1DollExecutor> dolls = _alldolls.get(itemobjId);
		if (dolls != null){
			dolls.clear();
			_alldolls.remove(itemobjId);
			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("DELETE FROM items_dolls WHERE item_objId=?");
				pstm.setInt(1, itemobjId);
				pstm.execute();
			} catch (SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}
	
	public void addDollTypes(L1DollExecutor dollExe,final int itemObjId) {
		ArrayList<L1DollExecutor> dolls = _alldolls.get(itemObjId);
		if (dolls == null){
			dolls = new ArrayList<L1DollExecutor>();
			_alldolls.put(itemObjId, dolls);
		}
		dolls.add(dollExe);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO items_dolls SET item_objId = ?, dollClassname = ?, value1 = ?, value2 = ?");
			pstm.setInt(1, itemObjId);
			pstm.setString(2, dollExe.getClassName());
			pstm.setInt(3, dollExe.getValue1());
			pstm.setInt(4, dollExe.getValue2());
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public L1DollExecutor getDollType(final String className,final int value1,final int value2,final String name,final String nameId){
		try {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("l1j.server.server.model.doll.");
            stringBuilder.append(className);

            final Class<?> cls = Class.forName(stringBuilder.toString());
            final L1DollExecutor exe = (L1DollExecutor) cls.getMethod("get").invoke(null);
            exe.set_power(value1, value2, 0);
            exe.set_powerClassName(className);
            exe.set_Name(name);
            exe.set_NameId(nameId);
            return exe;
        } catch (final ClassNotFoundException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (final IllegalArgumentException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (final IllegalAccessException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (final InvocationTargetException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (final SecurityException e) {
            _log.error(e.getLocalizedMessage(), e);
        } catch (final NoSuchMethodException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
		return null;
	}
	
}
