package l1j.server.server.model;

import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.game.GamblingTimeList;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.storage.GamblingLock;
import l1j.server.server.templates.L1Gam;
import l1j.server.server.templates.L1Gambling;

public class L1GamSpList {

	// 储存人物
	private final L1PcInstance _pc;

	public L1GamSpList(L1PcInstance pc) {
		_pc = pc;
	}

	private static GamblingTimeList _gam = GamblingTimeList.gam();

	/**
	 * <font color=#0000ff>清空特殊购物清单资讯</font><BR>
	 * 清空物件资讯释放记忆体<BR>
	 */
	public void clear() {
		// 赌场
		_gamList.clear();
		
		_gamSellList.clear();
	}

	// 赌场购物清单
	private final ArrayList<L1Gam> _gamList = new ArrayList<L1Gam>();

	/**
	 * <font color=#827B00>复制全部资料(赌场)</font>
	 * @param tg
	 */
	public void set_copyGam(ArrayList<L1Gam> gams) {
		// 清空原清单资料
		clear();
		
		_gamList.addAll(gams);
	}

	/**
	 * <font color=#0000ff>赌场购物清单全部物品</font>
	 * @return _spList 物品清单(List格式)
	 */
	public ArrayList<L1Gam> get_gamList() {
		return _gamList;
	}

	/**
	 * 加入赌场购物车
	 * @param order
	 * @param count
	 */
	public void addGamItem(int order, int count) {

		L1Gam gamItem = _gamList.get(order);

		// 单物品总价
		int price = (int) (Config.Gam_CHIP * count);

		checkGamShopItem(gamItem, count, price);
	}

	/**
	 * 执行命令
	 * @param gamItem 给予物品的资讯
	 * @param amount 给予数量
	 * @param totalPrice 消费额
	 */
	private void checkGamShopItem(L1Gam gamItem, int amount, int totalPrice) {

		// 取得人物金币数量
		long price = 0;
		L1ItemInstance priceItem = null;
			
		priceItem = _pc.getInventory().findItemId(L1ItemId.ADENA);
		
		if (priceItem != null) {
			price = priceItem.getCount();
		}
		if (totalPrice > price) {
			// 产生讯息封包(936  金币不足，无法购买。)
			_pc.sendPackets(new S_ServerMessage(936));
			
		} else {
			// 删除道具
			_pc.getInventory().removeItem(priceItem, totalPrice);
			
			// 取得人物背包
			L1PcInventory inv = _pc.getInventory();

			// 取回参赛者ID
			int npcGam1 = GamblingTimeList.gam().get_npcGam1().getNpcId();
			int npcGam2 = GamblingTimeList.gam().get_npcGam2().getNpcId();
			int npcGam3 = GamblingTimeList.gam().get_npcGam3().getNpcId();
			int npcGam4 = GamblingTimeList.gam().get_npcGam4().getNpcId();
			int npcGam5 = GamblingTimeList.gam().get_npcGam5().getNpcId();
			
			int itemId = 40309;
			int gamNo = gamItem.getGamNumId();
			int gamNpcId = gamItem.getGamNpcId();
			String gamNpcName = gamItem.getGamName();
			//int amount = count;

			// 比赛开始就无法买票
			if (_gam.get_isStart()) {
				return;
			}
			// 买票时间过了就无法买票
			if (!_pc.hasSkillEffect(123456)) {
				return;
			}
			if (_gam.get_gamId() != gamNo) {
				return;
			}

			if (gamNpcId == npcGam1) {
				// 追加赌场参赛者1 押注(累积)
				GamblingTimeList.gam().add_npcChip1((int) (amount * Config.Gam_CHIP));
			}
			else if (gamNpcId == npcGam2) {
				// 追加赌场参赛者2 押注(累积)
				GamblingTimeList.gam().add_npcChip2((int) (amount * Config.Gam_CHIP));
			}
			else if (gamNpcId == npcGam3) {
				// 追加赌场参赛者3 押注(累积)
				GamblingTimeList.gam().add_npcChip3((int) (amount * Config.Gam_CHIP));
			}
			else if (gamNpcId == npcGam4) {
				// 追加赌场参赛者4 押注(累积)
				GamblingTimeList.gam().add_npcChip4((int) (amount * Config.Gam_CHIP));
			}
			else if (gamNpcId == npcGam5) {
				// 追加赌场参赛者5 押注(累积)
				GamblingTimeList.gam().add_npcChip5((int) (amount * Config.Gam_CHIP));
			}
			
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			
			item.setCount(amount);// 设置数量
			item.setIdentified(true);// 设置为已鉴定
			item.setGamNo(gamNo);// 设置赌场场次编号
			item.setGamNpcId(gamNpcId);// 设置赌场场次NPC编号
			item.setGamNpcName(gamNpcName);
//			System.out.println("购入赛狗票数量:"+item.getCount()+"NPCID:"+item.getGamNpcId()+"NPCNAME:"+item.getGamNpcName());
			
			// 加入背包
			inv.storeItem(item);
		}
	}

	/**
	 * 完成赌场购物的判断
	 */
	public void checkGamShop() {
		// 清空原清单资料
		clear();
	}
	
	// 赌场商人 物品回收清单
	private final ArrayList<L1ItemInstance> _gamSellList = new ArrayList<L1ItemInstance>();

	/**
	 * <font color=#827B00>复制全部资料(赌场商人 物品回收)</font>
	 * @param tg
	 */
	public void set_copySellGam(ArrayList<L1ItemInstance> invList) {
		// 清空原清单资料
		clear();
		
		_gamSellList.addAll(invList);
	}

	/**
	 * <font color=#0000ff>赌场商人 回收全部物品清单</font>
	 * @return _spList 物品清单(List格式)
	 */
	public ArrayList<L1ItemInstance> get_gamSellList() {
		return _gamSellList;
	}

	/**
	 * 加入赌场商人 物品回收列表
	 * @param objid
	 * @param count
	 */
	public void addSellGamItem(int objid, int count) {
		boolean isOk = false;
		L1ItemInstance gamItem = _pc.getInventory().getItem(objid);

		for (L1ItemInstance chItem : _gamSellList) {
			if (chItem == gamItem) {
				isOk = true;
			}
		}
		
		if (isOk) {
			// 场次
			int gamId = gamItem.getGamNo();
			// 单物品总价
			int price = 0;

			L1Gambling gamInfo = GamblingLock.create().getGambling(gamId);
			
			if (gamInfo != null) {
				// NPCID相同
				if (gamInfo.get_npcid() == gamItem.getGamNpcId()) {

					price = (int) (Config.Gam_CHIP * gamInfo.get_rate()) * count;
				}
			}

			checkGamSellItem(gamItem, count, price);
		}

	}

	/**
	 * 执行命令
	 * @param gamItem 要移除的物品
	 * @param count 要移除的数量
	 * @param amount 给予数量
	 * @param mode 比赛模式
	 */
	private void checkGamSellItem(L1ItemInstance gamItem, int count, int amount) {
		if (gamItem == null) {
			return;
		}
		// 删除道具
		_pc.getInventory().removeItem(gamItem, count);
		
		// 取得人物背包
		L1PcInventory inv = _pc.getInventory();
		// 金币
		L1ItemInstance item = null;
		// 判断该场次模式
		item = ItemTable.getInstance().createItem(L1ItemId.ADENA);// 金币
		
		item.setCount(amount);// 设置数量
		
		// 加入背包
		inv.storeItem(item);
	}

	/**
	 * 完成赌场回收的判断
	 */
	public void checkGamSell() {
		// 清空原清单资料
		clear();
	}
	
}
