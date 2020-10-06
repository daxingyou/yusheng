package l1j.server.data.item_etcitem.reel;

import java.util.Random;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.CharactersItemStorage;

/**
 * <font color=#00800>武器属性強化卷軸</font><BR>
 * 
 * @see 增加武器 強化值<BR>
 * @author dexc
 * 
 */
public class ScrollAttrEnchantWeapon extends ItemExecutor {

	/**
	 *
	 */
	private ScrollAttrEnchantWeapon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ScrollAttrEnchantWeapon();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		// 對象OBJID
		final int targObjId = data[0];
//		System.out.println(targObjId);

		// 目標物品
		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem == null) {
			return;
		}
		
		if (_attrEnchantKind <= 0 || !(_attrEnchantKind == 1 || _attrEnchantKind == 2 || _attrEnchantKind == 4 || _attrEnchantKind == 8)){
			return;
		}
		if (tgItem.isSeal()) {
			pc.sendPackets(new S_SystemMessage(tgItem.getLogViewName() +"处于封印状态！"));
			return;
		}

		// final int safe_enchant = tgItem.getItem().get_safeenchant();

		// 0:無属性 1:地 2:火 4:水 8:風
		int oldAttrEnchantKind = tgItem.getAttrEnchantKind();
		int oldAttrEnchantLevel = tgItem.getAttrEnchantLevel();

		boolean isErr = false;
		
		// 取得物件觸發事件
		final int use_type = tgItem.getItem().getUseType();
		switch (use_type) {
		case 1:// 武器
				// 相同屬性強化直大於3
			if (oldAttrEnchantKind == _attrEnchantKind) {
				if (oldAttrEnchantLevel >= 3) {
					isErr = true;
				}
			}
			break;

		default:
			isErr = true;
			break;
		}

		if (isErr) {
			pc.sendPackets(new S_ServerMessage(79)); // 没有发生任何事情。
			return;
		}
		int chance = 100;
		//tgItem.setAttrEnchantCount(tgItem.getAttrEnchantCount() + 1);
		//if (tgItem.getAttrEnchantCount() >= 60 && tgItem.getAttrEnchantCount() <= 99){
			//chance = 1;
		//}else if(tgItem.getAttrEnchantCount() >= 100){
			//chance = 100;
		//}
		final String itemName = item.getItem().getName();
		pc.getInventory().removeItem(item, 1);

		final Random random = new Random();
		final int rnd = random.nextInt(100) + 1;
		if (chance >= rnd) {
			//tgItem.setAttrEnchantCount(0);
			pc.sendPackets(new S_SystemMessage("对 "+tgItem.getLogViewName()+" 附加强大的魔法力量成功。"));

			int newAttrEnchantLevel = oldAttrEnchantLevel + 1;

			if (oldAttrEnchantKind != _attrEnchantKind) {
				newAttrEnchantLevel = 1;
			}

			tgItem.setAttrEnchantKind(_attrEnchantKind);
			pc.getInventory().updateItem(tgItem,
					L1PcInventory.COL_ATTR_ENCHANT_KIND);
			pc.getInventory().saveItem(tgItem,
					L1PcInventory.COL_ATTR_ENCHANT_KIND);

			tgItem.setAttrEnchantLevel(newAttrEnchantLevel);
			pc.getInventory().updateItem(tgItem,
					L1PcInventory.COL_ATTR_ENCHANT_LEVEL);
			pc.getInventory().saveItem(tgItem,
					L1PcInventory.COL_ATTR_ENCHANT_LEVEL);
			/*
			try {
				CharactersItemStorage.create().updateItemAttrEnchantCount(tgItem);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			WriteLogTxt.Recording("属性卷强化成功记录", String.format("玩家  %s 使用 %s 强化 %s 成功", pc.getName(),itemName,tgItem.getLogViewName()));
		} else {
			try {
				CharactersItemStorage.create().updateItemAttrEnchantCount(tgItem);
			} catch (Exception e) {
				e.printStackTrace();
			}
			WriteLogTxt.Recording("属性卷强化成功记录", String.format("玩家  %s 使用 %s 强化 %s 失败", pc.getName(),itemName,tgItem.getLogViewName()));
			pc.sendPackets(new S_SystemMessage("对 "+tgItem.getLogViewName()+" 附加魔法力量失败。"));
		}
	}
	
	private int _attrEnchantKind = 0;
	
	@Override
	public void set_set(String[] set) {
		try {
			_attrEnchantKind = Integer.parseInt(set[1]);
		} catch (Exception e) {

		}
	}
}