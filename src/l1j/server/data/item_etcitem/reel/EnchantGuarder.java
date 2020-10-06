package l1j.server.data.item_etcitem.reel;

import java.util.Random; 
import l1j.server.data.cmd.EnchantExecutor;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class EnchantGuarder extends ItemExecutor {
	private EnchantGuarder() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new EnchantGuarder();
	}

	private int _enchantlv = 1;

	@Override
	public void set_set(String[] set) {
		if (set.length > 1) {
			try {
				_enchantlv = Integer.parseInt(set[1]);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int objectId = data[0];
		final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
		if (targetItem == null || targetItem.getItem().getItemId() != 25063) {
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String
					.valueOf(targetItem.getLogViewName()))).append("不是强化目标！只能强化(全职臂甲)")
					.toString()));
			return;
		}
		if (targetItem.isSeal()) {
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String
					.valueOf(targetItem.getLogViewName()))).append("处于封印状态！")
					.toString()));
			return;
		}
		
		if (targetItem.getEnchantLevel() >= 9) {
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String
					.valueOf(targetItem.getLogViewName()))).append("已经强化到顶级了！")
					.toString()));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		boolean isEnchant = false;
		final Random random = new Random();
		int rand = 100;
		if (targetItem.getEnchantLevel() > -6 && targetItem.getEnchantLevel() <= 1) {
			rand = 22;

		} else if (targetItem.getEnchantLevel() >= 1 && targetItem.getEnchantLevel() <= 2) {
			rand = 18;

		} else if (targetItem.getEnchantLevel() >= 2 && targetItem.getEnchantLevel() <= 3) {
			rand = 13;

		} else if (targetItem.getEnchantLevel() >= 3 && targetItem.getEnchantLevel() <= 4) {
			rand = 10;

		} else if (targetItem.getEnchantLevel() >= 4 && targetItem.getEnchantLevel() <= 5) {
			rand = 8;
			
		} else if (targetItem.getEnchantLevel() >= 5 && targetItem.getEnchantLevel() <= 6) {
			rand = 4;
			
		} else if (targetItem.getEnchantLevel() >= 6 && targetItem.getEnchantLevel() <= 7) {
			rand = 2;
			
		} else if (targetItem.getEnchantLevel() >= 7 && targetItem.getEnchantLevel() <= 8) {
			rand = 1;
			
		} else if (targetItem.getEnchantLevel() >= 8 && targetItem.getEnchantLevel() <= 9) {
			rand = 1;
		}

//		System.out.println("几率:" + rand);
//		System.out.println("aa:" + targetItem.getEnchantLevel());
		
		if (random.nextInt(100) < rand) {
			isEnchant = true;
		}

		if (isEnchant) {// 成功
			new EnchantExecutor().successEnchant(pc, targetItem, _enchantlv);

		} else {// 失败
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String
					.valueOf(targetItem.getLogViewName()))).append(" 并没有发生什么事情...")
					.toString()));
		}
	}
}