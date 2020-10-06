package l1j.server.server.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1DuBo {
	private static L1DuBo _instance;
	private static final Log _log = LogFactory.getLog(L1DuBo.class);
	/**
	 * 当前押注的所有玩家列表 不包括庄家
	 */
	private final HashMap<Integer,YaZhuTemp> _allmembers = new HashMap<Integer,YaZhuTemp>();
	/**
	 * 掉线存储
	 */
	private final HashMap<Integer, Integer> _onlemembers = new HashMap<Integer,Integer>();
	/**
	 * 随机
	 */
	private static final Random _random = new Random();
	/**
	 * 当前npc对象 功后期使用
	 */
	private L1NpcInstance _npc = null;
	/**
	 * 庄家对象
	 */
	private L1PcInstance _zhuangpc = null;
	/**
	 * 庄家的总数量
	 */
	private int _zhuangcount = 0;
	/**
	 * 押大的总数量
	 */
	private int _dacount = 0;
	/**
	 * 押小的总数量
	 */
	private int _xiaocount = 0;
	/**
	 * 返回当前还能押大的数量
	 * 
	 * @return
	 */
	public int getNextDaCount() {
		return _zhuangcount - _dacount + _xiaocount;
	}
	/**
	 * 返回当前还能押小的数量
	 * 
	 * @return
	 */
	public int getNextXiaoCount() {
		return _zhuangcount - _xiaocount + _dacount;
	}
	
	public static L1DuBo get(final L1NpcInstance npc){
		if (_instance == null){
			_instance = new L1DuBo(npc);
		}
		return _instance;
	}
	
	private class YaZhuTemp{
		private L1PcInstance _pc;
		
		public void setCha(final L1PcInstance pc){
			_pc = pc;
		}
		public L1PcInstance getCha(){
			return _pc;
		}
		private int _BetType = 0;

		public void setBetType(final int i) {
			_BetType = i;
		}

		public int getBetType() {
			return _BetType;
		}
		// 赌博押注数量
		private int _DuBoCount = 0;

		public void addDuBoCount(final int i) {
			_DuBoCount += i;
		}
		public int getDuBoCount() {
			return _DuBoCount;
		}
	}
	
	private L1DuBo(final L1NpcInstance npc) {
		_npc = npc;
	}
	
	class DuboThread extends Thread {
		/**
		 * 60秒抢庄时间
		 * 
		 * @throws InterruptedException
		 */
		private void QiangZhuang() throws InterruptedException {
			for (int loop = 60; loop > 0; loop--) {// 60秒抢庄时间
				L1DuBo.this.setNextLoop(loop);
				Thread.sleep(1000);
			}
			if (_npc != null){
				_npc.broadcastPacket(new S_NpcChatPacket(_npc, "抢庄停止 现在开始押注 .", 2));
			}
		}
		/**
		 * 60押注时间
		 * 
		 * @throws InterruptedException
		 */
		private void YaZhu() throws InterruptedException {
			for (int loop = 60; loop > 0; loop--) {// 60秒押注时间
				L1DuBo.this.setNextLoop(loop);
				Thread.sleep(1000);
			}
			if (_npc != null){
				_npc.broadcastPacket(new S_NpcChatPacket(_npc, "押注停止 .", 2));
			}
		}

		@Override
		public void run() {
			try {
				_allmembers.clear();//清空所有压住信息 方便下次压住
				
				L1DuBo.this.setStartOn(1);//开始抢庄
				this.QiangZhuang();//抢庄
				L1DuBo.this.setStartOn(2);//开始压住
				this.YaZhu();// 押注
				L1DuBo.this.setStartOn(3);//开始开住
				for (int i = 10; i > 0; i--) { // 倒计10秒后 开
					L1DuBo.this.setNextLoop(i);
					if (_npc != null){
						_npc.broadcastPacket(new S_NpcChatPacket(_npc, String.format("倒计时:%d秒", i), 2));
					}
					Thread.sleep(1000);
				}
				// 3颗骰子
				int dian1 = _random.nextInt(6) + 1;			
				int dian2 = _random.nextInt(6) + 1;
				int dian3 = _random.nextInt(6) + 1;

				if (L1DuBo.this._gmbaoz){
					dian1 = _random.nextInt(6) + 1;//隨機出個豹子
					dian2 = dian1;
					dian3 = dian1;
					L1DuBo.this._gmbaoz = false;
				}
				if (L1DuBo.this._gmda){
					dian1 = 4 + _random.nextInt(3);//隨機出4以上點 必大
					dian2 = 4 + _random.nextInt(3);//隨機出4以上點 必大
					dian3 = 4 + _random.nextInt(3);//隨機出4以上點 必大
					if (dian1 == dian2 &&  dian2 == dian3){//如果是豹子 重置点1
						while (dian1 == dian2) {
							dian1 = 4 +  _random.nextInt(3);
						}
					}
					L1DuBo.this._gmda = false;
				}
				if (L1DuBo.this._gmxiao){
					dian1 = _random.nextInt(3) + 1;//3
					dian2 = _random.nextInt(3) + 1;//3
					dian3 = _random.nextInt(3) + 1;//3
					if (dian1 == dian2 &&  dian2 == dian3){//如果是豹子 重置点1
						while (dian1 == dian2) {
							dian1 = _random.nextInt(3) + 1;//3
						}
					}
					L1DuBo.this._gmxiao = false;
				}
				if (_npc != null){
					_npc.broadcastPacket(new S_SkillSound(_npc.getId(), dian1 + 3203));
					Thread.sleep(4000);
					_npc.broadcastPacket(new S_SkillSound(_npc.getId(), dian2 + 3203));
					Thread.sleep(4000);
					_npc.broadcastPacket(new S_SkillSound(_npc.getId(), dian3 + 3203));
					Thread.sleep(4000);
				}
				final StringBuilder npcChatMsg = new StringBuilder();
				npcChatMsg.append(String.format("当前点数:%d、%d、%d  %d点", dian1,dian2,dian3,dian1 + dian2 + dian3));
				int XYbCount = 0;
				int ZYbCount = 0;

				if (dian1 == dian2 && dian2 == dian3) {// 3颗点一样大 豹子 庄家通吃
					ZYbCount = _zhuangcount + _dacount + _xiaocount;// 奖励规则
					npcChatMsg.append("豹子 庄家通吃.");
				} else if ((dian1 + dian2 + dian3) < 10) {// 3颗加起来小于10 为【小】
					for (final YaZhuTemp element : L1DuBo.this._allmembers.values()) {
						if (element.getBetType() == 2) { // 奖励所有 押 小的玩家
							XYbCount = element.getDuBoCount() * 2;// 闲家奖励 规则
							XYbCount -= XYbCount * 0.02;//收取2%
							if (element.getCha().getOnlineStatus() == 1){
								element.getCha().getInventory().storeItem(40308,XYbCount);
								element.getCha().sendPackets(new S_ServerMessage(143, "赌博大师","金币(" + XYbCount + ")"));
								WriteLogTxt.Recording("赌博记录","玩家:"+ element.getCha().getName() +"赢得金币(" + XYbCount + ")在线");
							}else{
								_onlemembers.put(element.getCha().getId(), XYbCount);
								WriteLogTxt.Recording("赌博记录","玩家:"+ element.getCha().getName() +"赢得金币(" + XYbCount + ")离线");
							}
						}
					}
					// 开【小】 庄家 补【小】吃 【大】
					ZYbCount = _zhuangcount - _xiaocount + _dacount;
					npcChatMsg.append("小");
				} else if ((dian1 + dian2 + dian3) >= 10) { // 3颗加起来大于或等于10// 为【大】;
					for (final YaZhuTemp element : L1DuBo.this._allmembers.values()) {
						if (element.getBetType() == 1) {// 奖励押 大 的玩家
							XYbCount = element.getDuBoCount() * 2;
							XYbCount -= XYbCount * 0.02;//收取2%
							if (element.getCha().getOnlineStatus() == 1){
								element.getCha().getInventory().storeItem(40308,XYbCount);
								element.getCha().sendPackets(new S_ServerMessage(143, "赌博大师","金币(" + XYbCount + ")"));
								WriteLogTxt.Recording("赌博记录","玩家:"+ element.getCha().getName() +"赢得金币(" + XYbCount + ")在线");
							}else{
								_onlemembers.put(element.getCha().getId(), XYbCount);
								WriteLogTxt.Recording("赌博记录","玩家:"+ element.getCha().getName() +"赢得金币(" + XYbCount + ")离线");
							}
						}
					}
					// 开【大】 庄家补【大】吃 【小】
					ZYbCount = _zhuangcount - _dacount + _xiaocount;
					npcChatMsg.append("大");
				}
				if (_npc != null){
					_npc.broadcastPacket(new S_NpcChatPacket(_npc, npcChatMsg.toString(), 2) );
				}
				if (_zhuangpc != null){
					if (ZYbCount > 0){
						//庄家扣除手续费 当前庄数量 大于抢庄的数量
						if (ZYbCount >= _zhuangcount - 100){
							ZYbCount -= (ZYbCount - _zhuangcount) * 0.02;
						}
						if (_zhuangpc.getOnlineStatus() == 1){
							_zhuangpc.getInventory().storeItem(40308, ZYbCount);
							_zhuangpc.sendPackets(new S_ServerMessage(143, "赌博大师","金币(" + ZYbCount + ")"));
							WriteLogTxt.Recording("赌博记录","玩家:"+ _zhuangpc.getName() +"收回庄家本钱金币(" + ZYbCount + ")在线");
						}else{
							_onlemembers.put(_zhuangpc.getId(), ZYbCount);
							WriteLogTxt.Recording("赌博记录","玩家:"+ _zhuangpc.getName() +"收回庄家本钱金币(" + ZYbCount + ")离线");
						}
					}
					L1World.getInstance().broadcastServerMessage(String.format("庄家:%s 收回了成本%d金币", _zhuangpc.getName(),ZYbCount));
				}
				// 完毕 初始化属性
				_allmembers.clear();//清空所有压住信息 方便下次压住
				_zhuangpc = null;
				_zhuangcount = 0;
				_dacount = 0;
				_xiaocount = 0;
				L1DuBo.this.setStartOn(0);
				L1DuBo.this.setNextLoop(0);
			} catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
	}
	public int getOnlineCount(final L1PcInstance pc){
		int count = 0;
		if (_onlemembers.containsKey(pc.getId())){
			count = _onlemembers.get(pc.getId());
			_onlemembers.remove(pc.getId());
		}
		return count;
	}
	private int _START_ON = 0;
	/**
	 * 0 空闲
	 * 1 抢庄
	 * 2 压住
	 * 3 开住
	 * @param o
	 */
	private void setStartOn(final int o){
		_START_ON = o;
	}
    /**
	 * 0 空闲
	 * 1 抢庄
	 * 2 压住
	 * 3 开住
     */
	public int getStartOn(){
		return _START_ON;
	}
	
	private int _nextloop = 0;
	
	private void setNextLoop(final int s){
		_nextloop = s;
	}
	private int getNextLoop(){
		return _nextloop;
	}
	/**
	 * 开始游戏
	 */
	private void StartGame() {
		final DuboThread dubo = new DuboThread();
		GeneralThreadPool.getInstance().execute(dubo);
	}
	/**
	 * 
	 * @param pc
	 * @param count
	 * @param type 0抢庄 1压大 2压小
	 */
	public synchronized void setDuBoPc(final L1PcInstance pc,final int count,final int type) {
		if (count <= 0){
			return;
		}
		if (count > 105000000) {
			pc.sendPackets(new S_SystemMessage("为了防止沉迷赌博 赌博数量不能超过1亿500万"));
			return;
		}
		final L1ItemInstance deaItem = pc.getInventory().findItemId(40308);
		if (deaItem == null || deaItem.getCount() < count) {
			pc.sendPackets(new S_SystemMessage("你的金币不足!"));
			return;
		}
		final long dea_count = deaItem.getCount();
		if (type == 0 && (getStartOn() == 0 || getStartOn() == 1)){
			if (_zhuangpc == null){
				if (count < 100000){
					pc.sendPackets(new S_SystemMessage("庄家数量不能低于10万!"));
					return;
				}
				if (pc.getInventory().removeItem(deaItem, count) == count){
					_zhuangpc = pc;
					_zhuangcount = count;
					this.StartGame();
					L1World.getInstance().broadcastServerMessage(String.format("%s使用了 %d金币坐庄 想赢金币的赶快去押注吧.", pc.getName(),_zhuangcount));
					final L1ItemInstance item = pc.getInventory().getItem(deaItem.getId());
					final long next_count = item == null ? 0 : item.getCount();
					WriteLogTxt.Recording("赌博记录","玩家:"+ pc.getName() +" 使用金币("+ count +")坐庄.原金币数量:" + dea_count + " 剩余金币数量:" + next_count);
				}
			}else{
				if (_zhuangcount >= 100000000) {
					pc.sendPackets(new S_SystemMessage("为了防止沉迷赌博 当前庄家数量已达到了1亿 不能再抢庄了"));
					return;
				}
				if (_zhuangpc.getId() == pc.getId()){
					if (count < 50000){
						pc.sendPackets(new S_SystemMessage("数量必须大于5万"));
						return;
					}
					if (pc.getInventory().removeItem(deaItem, count) == count){
						_zhuangpc = pc;
						_zhuangcount += count;
						L1World.getInstance().broadcastServerMessage(String.format("%s使用了 %d金币坐庄 想赢金币的赶快去押注吧.", pc.getName(),_zhuangcount));
						final L1ItemInstance item = pc.getInventory().getItem(deaItem.getId());
						final long next_count = item == null ? 0 : item.getCount();
						WriteLogTxt.Recording("赌博记录","玩家:"+ pc.getName() +" 使用金币("+ count +") 增加庄的本钱.原金币数量:" + dea_count + " 剩余金币数量:" + next_count);
					}
				}else {
					if (count >= _zhuangcount + 50000) {
						if (pc.getInventory().removeItem(deaItem, count) == count){
							final L1ItemInstance item = pc.getInventory().getItem(deaItem.getId());
							final long next_count = item == null ? 0 : item.getCount();
							WriteLogTxt.Recording("赌博记录","玩家:"+ pc.getName() +" 使用金币("+ count +") 抢庄.原金币数量:" + dea_count + " 剩余金币数量:" + next_count);
							if (_zhuangpc.getOnlineStatus() == 1){
								_zhuangpc.getInventory().storeItem(40308, _zhuangcount);
								_zhuangpc.sendPackets(new S_ServerMessage(143, "赌博大师","金币(" + _zhuangcount + ")"));
								WriteLogTxt.Recording("赌博记录","玩家:"+ _zhuangpc.getName() +" 被  " + pc.getName() + " 抢庄.已退还金币(" + _zhuangcount + ") 在线");
							}else{
								_onlemembers.put(_zhuangpc.getId(), _zhuangcount);
								WriteLogTxt.Recording("赌博记录","玩家:"+ _zhuangpc.getName() +" 被  " + pc.getName() + " 抢庄.已退还金币(" + _zhuangcount + ") 离线");
							}
							_zhuangcount = count;
							_zhuangpc = pc;// 重新定义新的庄家
							L1World.getInstance().broadcastServerMessage(String.format("%s使用了 %d金币坐庄 想赢金币的赶快去押注吧.", pc.getName(),_zhuangcount));
						}
					}else{
						pc.sendPackets(new S_SystemMessage("抢庄数量必须大于当前庄家数量的5万."));
					}
				}
			}
		}else if (type == 1 && getStartOn() == 2){
			if (count < 10000){
				pc.sendPackets(new S_SystemMessage("压住不能低于1万"));
				return;
			}
			if (_zhuangpc.getId() == pc.getId()) {
				pc.sendPackets(new S_SystemMessage("你是庄 不能押注."));
				return;
			}
			YaZhuTemp temp = _allmembers.get(pc.getId());
			if (temp != null && temp.getBetType() == 2) {
				pc.sendPackets(new S_SystemMessage("你已经押了【小】 不能再押【大】了."));
				return;
			}
			if (getNextDaCount() < count) {
				pc.sendPackets(new S_SystemMessage(String.format("当前压大最多还能压%d", getNextDaCount())));
				return;
			}
			if (pc.getInventory().removeItem(deaItem, count) == count){
				if (temp == null){
					temp = new YaZhuTemp();
					_allmembers.put(pc.getId(), temp);
				}
				_dacount += count;
				temp.addDuBoCount(count);
				temp.setBetType(1);
				temp.setCha(pc);
				pc.sendPackets(new S_SystemMessage(String.format("你使用了%d金币压大 你当前压住总数量%d",count, temp.getDuBoCount())));
				if (_npc != null){
					_npc.broadcastPacket(new S_NpcChatPacket(_npc, String.format("%s 押了:%d金币【大】", pc.getName(),count), 2));
				}
				final L1ItemInstance item = pc.getInventory().getItem(deaItem.getId());
				final long next_count = item == null ? 0 : item.getCount();
				WriteLogTxt.Recording("赌博记录","玩家:"+ pc.getName() +"使用金币(" + count + ") 压大 当前" + pc.getName() + "压大总数量:" + temp.getDuBoCount() + "原金币数量:" + dea_count + " 剩余金币数量:" + next_count);
			}
		}else if (type == 2 && getStartOn() == 2){
			if (count < 10000){
				pc.sendPackets(new S_SystemMessage("压住不能低于1万"));
				return;
			}
			if (_zhuangpc.getId() == pc.getId()) {
				pc.sendPackets(new S_SystemMessage("你是庄 不能押注."));
				return;
			}
			YaZhuTemp temp = _allmembers.get(pc.getId());
			if (temp != null && temp.getBetType() == 1) {
				pc.sendPackets(new S_SystemMessage("你已经押了【大】 不能再押【小】了."));
				return;
			}
			if (getNextXiaoCount() < count) {
				pc.sendPackets(new S_SystemMessage(String.format("当前压小最多还能压%d", getNextXiaoCount())));
				return;
			}
			if (pc.getInventory().removeItem(deaItem, count) == count){
				if (temp == null){
					temp = new YaZhuTemp();
					_allmembers.put(pc.getId(), temp);
				}
				_xiaocount += count;
				temp.addDuBoCount(count);
				temp.setBetType(2);
				temp.setCha(pc);
				pc.sendPackets(new S_SystemMessage(String.format("你使用了%d金币压小 你当前压住总数量%d",count, temp.getDuBoCount())));
				if (_npc != null){
					_npc.broadcastPacket(new S_NpcChatPacket(_npc, String.format("%s 押了:%d金币【小】", pc.getName(),count), 2));
				}
				final L1ItemInstance item = pc.getInventory().getItem(deaItem.getId());
				final long next_count = item == null ? 0 : item.getCount();
				WriteLogTxt.Recording("赌博记录","玩家:"+ pc.getName() +"使用金币(" + count + ") 压小 当前" + pc.getName() + "压小总数量:" + temp.getDuBoCount() + "原金币数量:" + dea_count + " 剩余金币数量:" + next_count);
			}
		}
	}
	public String[] MsgDate(L1PcInstance pc) {
		String zpcname = "";
		String pcstyle = "";
		String starton = "";
		int count = 0;
		
		if (_zhuangpc != null){
			zpcname = _zhuangpc.getName();
			if (pc.getId() == _zhuangpc.getId()) {
				pcstyle = "庄";
				count = _zhuangcount;
			}else{
				final YaZhuTemp temp = _allmembers.get(pc.getId());
				if (temp != null){
					if (temp.getBetType() == 1) {
						pcstyle = "大";
					} else if (temp.getBetType() == 2) {
						pcstyle = "小";
					} else {
						pcstyle = "";
					}
					count = temp.getDuBoCount();
					temp.setCha(pc);
				}
			}
		}
		
		if (getStartOn() == 0){
			starton = "空闲";
		}else if (getStartOn() == 1){
			starton = "抢庄中";
		}else if (getStartOn() == 2){
			starton = "压住中";
		}else if (getStartOn() == 3){
			starton = "开住中";
		}
		
		final String[] md = new String[] {zpcname, insertComma(_zhuangcount),starton, String.valueOf(getNextLoop()),
				insertComma(_dacount), insertComma(_xiaocount),
				String.valueOf(pcstyle), insertComma(count),
				insertComma(this.getNextDaCount()),
				insertComma(this.getNextXiaoCount()) };
		return md;
	}
	/**
	 * 金额格式化
	 * 
	 * @param s
	 *            金额
	 * @return 格式后的金额 999,000,000,000
	 */
	private String insertComma(int s) {
		// 如歌金额大于 20亿 可能会有错误出现
		final NumberFormat formater = new DecimalFormat("###,###,###,###");
		return formater.format(s);
	}
	

	private boolean _gmbaoz = false;
	
	private boolean _gmda = false;
	
	private boolean _gmxiao = false;

	public void setGmBaoz(final boolean b){
		_gmbaoz = b;
		_gmda = false;
		_gmxiao = false;
	}
	public void setGmDa(final boolean b){
		_gmbaoz = false;
		_gmda = b;
		_gmxiao = false;
	}
	public void setGmXiao(final boolean b){
		_gmbaoz = false;
		_gmda = false;
		_gmxiao = b;
	}
}