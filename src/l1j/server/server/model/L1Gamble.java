package l1j.server.server.model;

import java.util.ArrayList;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.game.GamblingTime;
import l1j.server.server.model.game.GamblingTimeList;
import l1j.server.server.model.game.T_Gambling;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NoSell;
import l1j.server.server.serverpackets.S_ShopBuyListGam;
import l1j.server.server.serverpackets.S_ShopSellListGam;
import l1j.server.server.storage.GamblingLock;
import l1j.server.server.templates.L1Gam;
import l1j.server.server.templates.L1Gambling;

/**
 * 波金 波丽 赛西(奇岩赌场)
 * @author dexc
 *
 */
public class L1Gamble{

	// html档案名称
	private String _htmlid = null;

	// 文字资料
	private String[] _data = null;
	
	private boolean _isStart = false;

	private static GamblingTimeList _gamlist = GamblingTimeList.gam();
	
//	L1NpcInstance npcGam1 = _gam.get_npcGam1();
//	L1NpcInstance npcGam2 = _gam.get_npcGam2();
//	L1NpcInstance npcGam3 = _gam.get_npcGam3();
//	L1NpcInstance npcGam4 = _gam.get_npcGam4();
//	L1NpcInstance npcGam5 = _gam.get_npcGam5();
	
	private static L1Gamble instance;
	
	public static L1Gamble getInstance()
	{
		if(instance == null)
		{
			instance = new L1Gamble();
		}
		return instance;
	}
	public void setGambingHtml(boolean flg){
		_isStart = flg;
	}
	
	public boolean isGambingHtml(){
		return _isStart;
	}
	
	public void buytickets(L1NpcInstance npc, L1PcInstance pc) {

		L1NpcInstance npcGam1 = _gamlist.get_npcGam1();
		L1NpcInstance npcGam2 = _gamlist.get_npcGam2();
		L1NpcInstance npcGam3 = _gamlist.get_npcGam3();
		L1NpcInstance npcGam4 = _gamlist.get_npcGam4();
		L1NpcInstance npcGam5 = _gamlist.get_npcGam5();

		if (_gamlist.get_isWaiting()) {
			// 比赛已经开始
			if (_gamlist.get_isStart()) {
				_htmlid = "maeno3";
				// 产生HTML视窗封包
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
				
			} else {
				// 物件不为空
				if (npcGam1 != null && npcGam2 != null && 
						npcGam3 != null && npcGam4 != null && npcGam5 != null ) {
					// 取回场次编号
					int gamId = _gamlist.get_gamId();
					// 建立新的传回清单
					ArrayList<L1Gam> gams = new ArrayList<L1Gam>();
					
					
					L1Gam gam1 = new L1Gam();
					gam1.setGamID(gamId);
					gam1.setGamNpcId(npcGam1.getNpcId());
					gam1.setGamNpcName(npcGam1.getName());			
//					int[] gam1 = new int[]{npcGam1.getNpcId(), gamId};
					gams.add(gam1);
					
//					int[] gam2 = new int[]{npcGam2.getNpcId(), gamId};
					L1Gam gam2 = new L1Gam();
					gam2.setGamID(gamId);
					gam2.setGamNpcId(npcGam2.getNpcId());
					gam2.setGamNpcName(npcGam2.getName());		
					gams.add(gam2);
					
//					int[] gam3 = new int[]{npcGam3.getNpcId(), gamId};
					L1Gam gam3 = new L1Gam();
					gam3.setGamID(gamId);
					gam3.setGamNpcId(npcGam3.getNpcId());
					gam3.setGamNpcName(npcGam3.getName());		
					gams.add(gam3);
					
//					int[] gam4 = new int[]{npcGam4.getNpcId(), gamId};
					L1Gam gam4 = new L1Gam();
					gam4.setGamID(gamId);
					gam4.setGamNpcId(npcGam4.getNpcId());
					gam4.setGamNpcName(npcGam4.getName());		
					gams.add(gam4);
					
//					int[] gam5 = new int[]{npcGam5.getNpcId(), gamId};
					L1Gam gam5 = new L1Gam();
					gam5.setGamID(gamId);
					gam5.setGamNpcId(npcGam5.getNpcId());
					gam5.setGamNpcName(npcGam5.getName());		
					gams.add(gam5);
					
					// 记忆清单
					pc.getGamSplist().set_copyGam(gams);
					// 送出封包
					pc.sendPackets(new S_ShopSellListGam(npc.getId(), gams));
					pc.setSkillEffect(123456, 20000);
				} else {
					_htmlid = "maeno2";
					// 产生HTML视窗封包
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
				}
			}
			
		} else {
			if (isGambingHtml()) {
				_htmlid = "maeno5";
			}else {
				_htmlid = "maeno1";
			}		
			// 产生HTML视窗封包
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
		}
	}
	
	public void selltickets(L1NpcInstance npc, L1PcInstance pc)
	{
		ArrayList<L1ItemInstance> list = sellList(pc);
		if (list.size() <= 0) {
			pc.sendPackets(new S_NoSell(npc));
		} else {
			// 记忆清单
			pc.getGamSplist().set_copySellGam(list);
			// 送出封包
			pc.sendPackets(new S_ShopBuyListGam(npc.getId(), list));
		}
	}
	
	public void gamstatus(L1NpcInstance npc, L1PcInstance pc) {

		L1NpcInstance npcGam1 = _gamlist.get_npcGam1();
		L1NpcInstance npcGam2 = _gamlist.get_npcGam2();
		L1NpcInstance npcGam3 = _gamlist.get_npcGam3();
		L1NpcInstance npcGam4 = _gamlist.get_npcGam4();
		L1NpcInstance npcGam5 = _gamlist.get_npcGam5();

		if (npcGam1 != null && npcGam2 != null && 
				npcGam3 != null && npcGam4 != null && npcGam5 != null ) {
			_htmlid = "maeno4";
			_data = status();		
			// 产生HTML视窗封包
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid, _data));			
		} else {
			_htmlid = "maeno5";
			// 产生HTML视窗封包
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
		}
	}

	/**
	 * 检查可卖出的食人妖精竞赛票
	 * @param pc
	 * @return
	 */
	private static ArrayList<L1ItemInstance> sellList(L1PcInstance pc) {
		// 建立新的传回清单
		ArrayList<L1ItemInstance> gams = new ArrayList<L1ItemInstance>();
		// 取回PC身上所有食人妖精竞赛票
		L1ItemInstance[] gamItems = pc.getInventory().findGam();
		if (gamItems.length <= 0) {
			return gams;
		}

		for (L1ItemInstance gItem : gamItems) {
			int gamId = gItem.getGamNo();
			L1Gambling gamInfo = GamblingLock.create().getGambling(gamId);
			if (gamInfo != null) {
				// NPCID相同
				if (gamInfo.get_npcid() == gItem.getGamNpcId()) {
					// 加入回收清单
					gams.add(gItem);
				}
			}
		}

		return gams;
	}

	/**
	 * 查询参赛食人妖精的记录
	 * @return 
	 */
	private static String[] status() {
		// 取回全纪录
		L1Gambling[] list = GamblingLock.create().getGamblingList();
		// 取回参赛者
		L1NpcInstance npcGam1 = _gamlist.get_npcGam1();
		L1NpcInstance npcGam2 = _gamlist.get_npcGam2();
		L1NpcInstance npcGam3 = _gamlist.get_npcGam3();
		L1NpcInstance npcGam4 = _gamlist.get_npcGam4();
		L1NpcInstance npcGam5 = _gamlist.get_npcGam5();
		// 获胜场数
		int a1 = 0;
		int a2 = 0;
		int a3 = 0;
		int a4 = 0;
		int a5 = 0;
		
		// 取回场次编号
		int gamId = _gamlist.get_gamId();
		
		for (L1Gambling gItem :list) {
			// 取回50场内纪录
			if ((gamId - 50) <= gItem.get_id()) {
				if (gItem.get_npcid() == npcGam1.getNpcId()) {
					a1++;
				}
				if (gItem.get_npcid() == npcGam2.getNpcId()) {
					a2++;
				}
				if (gItem.get_npcid() == npcGam3.getNpcId()) {
					a3++;
				}
				if (gItem.get_npcid() == npcGam4.getNpcId()) {
					a4++;
				}
				if (gItem.get_npcid() == npcGam5.getNpcId()) {
					a5++;
				}
			}
		}
		// 状况判断
		String ac1 = "$370";// 预设 不好
		String ac2 = "$370";// 预设 不好
		String ac3 = "$370";// 预设 不好
		String ac4 = "$370";// 预设 不好
		String ac5 = "$370";// 预设 不好
		// NO1
		if (a1 <= 50 && a1 >= 35) {
			ac1 = "$368";// 好
			
		} else if (a1 < 35 && a1 >= 10) {
			ac1 = "$369";// 普通
		}
		// NO2
		if (a2 <= 50 && a2 >= 35) {
			ac2 = "$368";// 好
			
		} else if (a2 < 35 && a2 >= 10) {
			ac2 = "$369";// 普通
		}
		// NO3
		if (a3 <= 50 && a3 >= 35) {
			ac3 = "$368";// 好
			
		} else if (a3 < 35 && a3 >= 10) {
			ac3 = "$369";// 普通
		}
		// NO4
		if (a4 <= 50 && a4 >= 35) {
			ac4 = "$368";// 好
			
		} else if (a4 < 35 && a4 >= 10) {
			ac4 = "$369";// 普通
		}
		// NO5
		if (a5 <= 50 && a5 >= 35) {
			ac5 = "$368";// 好
			
		} else if (a5 < 35 && a5 >= 10) {
			ac5 = "$369";// 普通
		}

		// 胜率
		String ao1 = String.valueOf((a1 / 50.0) * 100.0);
		String ao2 = String.valueOf((a2 / 50.0) * 100.0);
		String ao3 = String.valueOf((a3 / 50.0) * 100.0);
		String ao4 = String.valueOf((a4 / 50.0) * 100.0);
		String ao5 = String.valueOf((a5 / 50.0) * 100.0);
		
		npcGam1 = _gamlist.get_npcGam1();
		npcGam2 = _gamlist.get_npcGam2();
		npcGam3 = _gamlist.get_npcGam3();
		npcGam4 = _gamlist.get_npcGam4();
		npcGam5 = _gamlist.get_npcGam5();
		
		String[] info = new String[] {
				npcGam1.getNameId(), ac1, ao1,
				npcGam2.getNameId(), ac2, ao2,
				npcGam3.getNameId(), ac3, ao3,
				npcGam4.getNameId(), ac4, ao4,
				npcGam5.getNameId(), ac5, ao5
			};
		/*
		 * 名称 / 状态 / 胜率
		 * <var src="#0"> <var src="#1"> <var src="#2">
		 * <var src="#3"> <var src="#4"> <var src="#5">
		 * <var src="#6"> <var src="#7"> <var src="#8">
		 * <var src="#9"> <var src="#10"> <var src="#11">
		 * <var src="#12"> <var src="#13"> <var src="#14">
		 */
		
		return info;
	}

}
