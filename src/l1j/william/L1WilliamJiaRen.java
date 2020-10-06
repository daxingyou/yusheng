/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.william;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.Maps;

public class L1WilliamJiaRen {

	private static Map<String, L1PcInstance> _logout;
	
	private static L1WilliamJiaRen _instance;
	
	public static L1WilliamJiaRen getInstance() {
		if (_instance == null) {
			_instance = new L1WilliamJiaRen();
		}
		return _instance;
	}
	
	private L1WilliamJiaRen() {
		_logout = Maps.newHashMap(); 
	}
	
	public void addlogout(String accname,L1PcInstance pc) {
		_logout.put(accname, pc);
	}
	
	public L1PcInstance getTemplate(String AccountName) {
		if (_logout.containsKey(AccountName)) {
			return _logout.get(AccountName);
		}
		return null;
	}
	
	public void remove(String AccountName) {
		_logout.remove(AccountName);
	}
	
	public Collection<L1PcInstance> getJiaRenAllPlayers() {
		return Collections.unmodifiableCollection(_logout.values());
	}
	
}