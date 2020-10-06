package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javolution.util.FastMap;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.CharaterTradeStorage;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1CharaterTrade;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharaterTradeTable implements CharaterTradeStorage {

	private static final Log _log = LogFactory.getLog(CharaterTradeTable.class);
	
	private final Map<Integer,L1CharaterTrade> _alltemps = new FastMap<Integer,L1CharaterTrade>();
	
	private final Map<Integer,L1PcInstance> _allpcs = new FastMap<Integer,L1PcInstance>();
	
	private int _nextId = 1000;
	
	@Override
	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_trade");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int numId = rs.getInt("id");
				final String char_name = rs.getString("char_name");
				final int char_objId = rs.getInt("char_objId");
				final int char_level = rs.getInt("level");
				final int char_type = rs.getInt("Type");
				final int char_sex = rs.getInt("Sex");
				final int money_count = rs.getInt("money_count");
				final int by_objId = rs.getInt("by_objId");
				final int state = rs.getInt("state");
				
				final L1CharaterTrade tmp = new L1CharaterTrade();
				tmp.set_id(numId);
				tmp.setName(char_name);
				tmp.set_char_objId(char_objId);
				tmp.setLevel(char_level);
				tmp.set_Type(char_type);
				tmp.set_Sex(char_sex);
				tmp.set_money_count(money_count);
				tmp.set_by_objId(by_objId);
				tmp.set_state(state);
				
				if (numId > _nextId){
					_nextId = numId;
				}
				
				_alltemps.put(numId, tmp);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		_log.info("載入character_trade數量: " + _alltemps.size() + "(" + timer.get() + "ms)");
	}
	
	private Collection<L1CharaterTrade> _allCharterTradeValues;

	@Override
	public Collection<L1CharaterTrade> getAllCharaterTradeValues() {
		Collection<L1CharaterTrade> vs = _allCharterTradeValues;
		return (vs != null) ? vs : (_allCharterTradeValues = Collections
				.unmodifiableCollection(_alltemps.values()));
	}
	
	@Override
	public int get_nextId(){
		_nextId ++;
		return _nextId;
	}
	
	@Override
	public boolean updateBindChar(final int objId,final int state){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET `CharBind`=? WHERE `objid`=?");
			pstm.setInt(++i, state);
			pstm.setInt(++i, objId);
			pstm.execute();
		}catch(SQLException e){
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}finally{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return true;
	}
	
	@Override
	public void updateCharAccountName(final int objId,final String accountName){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET `CharBind`=0,`account_name`=? WHERE `objid`=?");
			pstm.setString(++i, accountName);
			pstm.setInt(++i, objId);
			pstm.execute();
		}catch(SQLException e){
			_log.error(e.getLocalizedMessage(), e);
		}finally{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	@Override
	public boolean addCharaterTrade(final L1CharaterTrade charaterTrade){
		if (charaterTrade == null){
			return false;
		}
		if (_alltemps.containsKey(charaterTrade.get_id())){
			return false;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_trade SET `id`=?,`char_name`=?,`char_objId`=?,`level`=?,`Type`=?,`Sex`=?,`money_count`=?,`by_objId`=?,`state`=?");
			pstm.setInt(++i, charaterTrade.get_id());
			pstm.setString(++i, charaterTrade.getName());
			pstm.setInt(++i, charaterTrade.get_char_objId());
			pstm.setInt(++i, charaterTrade.getLevel());
			pstm.setInt(++i, charaterTrade.get_Type());
			pstm.setInt(++i, charaterTrade.get_Sex());
			pstm.setInt(++i, charaterTrade.get_money_count());
			pstm.setInt(++i, charaterTrade.get_by_objId());
			pstm.setInt(++i, charaterTrade.get_state());

			pstm.execute();

			_alltemps.put(charaterTrade.get_id(), charaterTrade);
		}catch(SQLException e){
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}finally{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return true;
	}
	
	@Override
	public void updateCharaterTrade(final L1CharaterTrade charaterTrade,final int state){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE character_trade SET `state`=? WHERE `id`=?");
			pstm.setInt(++i, state);
			pstm.setInt(++i, charaterTrade.get_id());
			pstm.execute();
			
			charaterTrade.set_state(state);
			if (_allpcs.containsKey(charaterTrade.get_char_objId())){
				_allpcs.remove(charaterTrade.get_char_objId());
			}
		}catch(SQLException e){
			_log.error(e.getLocalizedMessage(), e);
		}finally{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	@Override
	public void loadCharacterName(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT * FROM characters WHERE charBind = 0 AND account_name=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, pc.getAccountName());
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int c_ObjId = rs.getInt("objid");
				final Timestamp deleteTime =rs.getTimestamp("DeleteTime");
				final int level = rs.getInt("level");
				final String name = rs.getString("char_name");
				final int char_type = rs.getInt("Type");
				final int char_sex = rs.getInt("Sex");
				
				if (c_ObjId != pc.getId() && deleteTime == null){
					final L1CharaterTrade tmp = new L1CharaterTrade();
					tmp.setLevel(level);
					tmp.set_char_objId(c_ObjId);
					tmp.set_Type(char_type);
					tmp.set_Sex(char_sex);
					tmp.setName(name);
					
					pc.addTempObject(tmp);
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	@Override
	public L1CharaterTrade getCharaterTrade(final int id){
		return _alltemps.get(id);
	}
	@Override
	public L1PcInstance getPcInstance(final int objId){
		if (_allpcs.containsKey(objId)){
			return _allpcs.get(objId);
		}
		final L1PcInstance pc = CharacterTable.getInstance().loadCharacter(objId);
		if (pc == null){
			return null;
		}
		pc.getInventory().loadItemtrades();
		_allpcs.put(objId, pc);
		return pc;
	}
	
}
