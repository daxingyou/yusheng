package l1j.server.server.datatables.storage;

import java.util.Collection;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1CharaterTrade;

public interface CharaterTradeStorage {

	/**初始化加载*/
	public abstract void load();
	
	/**获取所有角色交易*/
	public abstract Collection<L1CharaterTrade> getAllCharaterTradeValues();
	
	/**获取编号*/
	public abstract int get_nextId();
	
	/**增加角色交易*/
	public abstract boolean addCharaterTrade(final L1CharaterTrade charaterTrade);
	
	/**
	 * 更新角色交易
	 * <br>
	 * state 0:普通 1:已交易未领取 2:已交易已领取 3:已撤销
	 */
	public abstract void updateCharaterTrade(final L1CharaterTrade charaterTrade,final int state);
	
	/**
	 * 加载当前账号内的所有角色不包括自己
	 */
	public abstract void loadCharacterName(final L1PcInstance pc);
	
	/**
	 * 更新人物的绑定状态
	 * @param objId
	 * @return
	 */
	public abstract boolean updateBindChar(final int objId,final int state);
	
	/**
	 * 获取交易的简易人物
	 * @param objId
	 * @return
	 */
	public abstract L1PcInstance getPcInstance(final int objId);
	
	public abstract L1CharaterTrade getCharaterTrade(final int id);
	
	public abstract void updateCharAccountName(final int objId,final String accountName);
}
