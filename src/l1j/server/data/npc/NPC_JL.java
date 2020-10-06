package l1j.server.data.npc;

import java.util.Map;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.lock.ShouBaoReading;
import l1j.server.server.datatables.lock.ShouShaReading;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1ShouBaoTemp;
import l1j.server.server.templates.L1ShouShaTemp;

/**
 * 首杀首爆查询
 * 
 * QQ：1043567675
 * by：亮修改
 * 2020年7月9日
 * 上午10:19:24
 */
public class NPC_JL extends NpcExecutor {

	private NPC_JL() {

	}

	@Override
	public int type() {
		return 3;
	}

	public static NpcExecutor get() {
		return new NPC_JL();
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jlcx"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("shou_bao_cx")) {
			ShouBaoChaXun(pc);
		}
		if (cmd.equalsIgnoreCase("shou_sha_cx")) {
			ShouShaChaXun(pc);
		}
	}
	
	private void ShouShaChaXun(L1PcInstance pc) {
		final Map<Integer, L1ShouShaTemp> shoushaMaps = ShouShaReading.get()
				.getShouShaMaps();
		if (shoushaMaps != null) {
			final String[] data = new String[153];
			data[0] = String.valueOf(ShouShaReading.get().getCount());
			data[1] = String.valueOf(ShouShaReading.get().getAmcount0());
			data[2] = String.valueOf(ShouShaReading.get().getAmcount1());
			int i = 3;
			for (final L1ShouShaTemp tmp : shoushaMaps.values()) {
				if (i >= 153) {
					break;
				}
				data[i] = String.format("怪物名称:%s", tmp.getNpcName());
				data[i + 1] = String.format("首杀奖励:%s(%d)",
						tmp.getGiveItemName(), tmp.getCount());
				if (tmp.getObjId() > 0) {
					data[i + 2] = String.format("获得玩家:%s", tmp.getName());
				} else {
					data[i + 2] = "获得玩家:暂无";
				}
				i += 3;
			}
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "shousha", data));
		}
	}

	private void ShouBaoChaXun(L1PcInstance pc) {
		final Map<Integer, L1ShouBaoTemp> shoubaoMaps = ShouBaoReading.get()
				.getShouBaoMaps();
		if (shoubaoMaps != null) {
			final String[] data = new String[153];
			data[0] = String.valueOf(ShouBaoReading.get().getCount());
			data[1] = String.valueOf(ShouBaoReading.get().getAmcount0());
			data[2] = String.valueOf(ShouBaoReading.get().getAmcount1());
			int i = 3;
			for (final L1ShouBaoTemp tmp : shoubaoMaps.values()) {
				if (i >= 153) {
					break;
				}
				data[i] = String.format("物品名称:%s", tmp.getItemName());
				data[i + 1] = String.format("首爆奖励:%s(%d)",
						tmp.getGiveItemName(), tmp.getGiveItemCount());
				if (tmp.getObjId() > 0) {
					data[i + 2] = String.format("获得玩家:%s", tmp.getName());
				} else {
					data[i + 2] = "获得玩家:暂无";
				}
				i += 3;
			}
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "shoubao", data));
		}
	}
}
