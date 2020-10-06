//package l1j.server.data.item_etcitem;
//
//import l1j.server.data.executor.ItemExecutor;
//import l1j.server.server.model.L1Teleport;
//import l1j.server.server.model.Instance.L1ItemInstance;
//import l1j.server.server.model.Instance.L1PcInstance;
//import l1j.server.server.serverpackets.S_NPCTalkReturn;
//import l1j.server.server.serverpackets.S_Paralysis;
//import l1j.server.server.serverpackets.S_SystemMessage;
//
//public class GuaJiFu extends ItemExecutor {
//	/**
//	 *
//	 */
//	private GuaJiFu() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public static ItemExecutor get() {
//		return new GuaJiFu();
//	}
//
//	/**
//	 * 道具物件执行
//	 * 
//	 * @param data
//	 *            参数
//	 * @param pc
//	 *            执行者
//	 * @param item
//	 *            物件
//	 */
//	@Override
//	public void execute(final int[] data, final L1PcInstance pc,
//			final L1ItemInstance item) {
//		if (pc.getMapId() != 53 && pc.getMapId() != 54 && pc.getMapId() != 55 
//				&& pc.getMapId() != 56) {
//			pc.sendPackets(new S_SystemMessage("\\aD非挂机地图无法开启！"));
//			return;
//		}
//		if (!pc.isActived()) {
//			// pc.startAI();
//			pc.startAI();
//			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "gjks"));
//			pc.sendPackets(new S_SystemMessage("\\aD挂机已经启动，若想结束请再次双击"));
//		} else {
//			pc.setActived(false);
//			pc.sendPackets(new S_SystemMessage("\\aD自动挂机已经结束。"));
//			try {
//				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, 0, true));
//				Thread.sleep(200);
//				final int x = pc.getX();
//				final int y = pc.getY();
//				final short map = pc.getMapId();
//				final int h = pc.getHeading();
//				L1Teleport.teleport(pc, x, y, map, h, false);
//				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, h, false));
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//}
package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SystemMessage;

public class GuaJiFu extends ItemExecutor {
	/**
	 *
	 */
	private GuaJiFu() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GuaJiFu();
	}

	/**
	 * 道具物件执行
	 * 
	 * @param data
	 *            参数
	 * @param pc
	 *            执行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
//		if (pc.getMapId() != 53 && pc.getMapId() != 54 && pc.getMapId() != 55
//				&& pc.getMapId() != 56) {
//			pc.sendPackets(new S_SystemMessage("\\aD非挂机地图无法开启！"));
//			return;
//		}
		if (!pc.isActived()) {
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "gjks"));

		} else {
			pc.setActived(false);
			//pc.setskillAuto_gj(false); // 关闭挂机状态
			pc.sendPackets(new S_SystemMessage("\\aD 自動狩獵已停止。"));
			pc.removeSkillEffect(L1SkillId.STATUS_BRAVE);
			try {
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, 0, true));
				Thread.sleep(200);
				final int x = pc.getX();
				final int y = pc.getY();
				final short map = pc.getMapId();
				final int h = pc.getHeading();
				L1Teleport.teleport(pc, x, y, map, h, false);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, h, false));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
