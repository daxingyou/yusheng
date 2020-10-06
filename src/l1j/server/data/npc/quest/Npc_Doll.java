package l1j.server.data.npc.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.DollPowerTable;
import l1j.server.server.datatables.DollXiLianTable;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.doll.L1DollExecutor;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Doll;
import l1j.server.server.templates.L1DollItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Doll extends NpcExecutor {
	private static final Log _log = LogFactory.getLog(Npc_Doll.class);
	private final static Random _random = new Random();
	private Npc_Doll() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Doll();
	}

	@Override
	public int type() {
		return 3;
	}
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollxl0"));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("sel_doll")) {
			pc.sendPackets(new S_RetrieveList(pc, npc));
		}else if (cmd.equalsIgnoreCase("sel_doll_ok")) {
			final int itemobjId = pc.getTempID();
			if (itemobjId <= 0){
				return;
			}
			final L1ItemInstance item = pc.getInventory().getItem(itemobjId);
			if (item == null){
				pc.setTempID(0);
				return;
			}
			final String[] data = new String[5];
			data[0] = item.getItem().getName();
			final ArrayList<L1DollExecutor> lists = DollXiLianTable.get().getDollTypes(itemobjId);
			int n = 1;
			if (lists != null && !lists.isEmpty()){
				for(final L1DollExecutor exe: lists){
					if (n >= 5){
						break;
					}
					if (exe.getValue2() != 0){
						data[n] = String.format(exe.getName(),exe.getValue1(),exe.getValue2());
					}else{
						data[n] = String.format(exe.getName(),exe.getValue1());
					}
					n++;
				}
			}
			for(;n < 5;n++){
				data[n] = "空";
			}
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollxl1",data));
		}else if (cmd.equalsIgnoreCase("doll_xl")) {
			final int itemobjId = pc.getTempID();
			//pc.setTempID(0);
			if (itemobjId <= 0){
				return;
			}
			final L1ItemInstance item = pc.getInventory().getItem(itemobjId);
			if (item == null){
				pc.setTempID(0);
				return;
			}
			if (item.isXiLianIng()){
				pc.sendPackets(new S_SystemMessage("\\F3正在洗练中"));
				return;
			}
			for (Object babyObject : pc.getPetList().values()) {
				if (babyObject instanceof L1BabyInstance) {
					L1BabyInstance baby = (L1BabyInstance) babyObject;
					if (item.getId() == baby.getItemObjId()) {
						// \f1%0舍他人让。
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}
				}
			}
			final L1Doll type = DollPowerTable.get().get_type(item.getItem().getItemId());
			if (type == null){
				pc.setTempID(0);
				return;
			}
			if (!pc.getInventory().checkItem(49201, 1)){
				pc.sendPackets(new S_SystemMessage("\\F2魔法娃娃洗练币不足."));
				return;
			}
			item.setXiLianIng(true);
			
			pc.getInventory().consumeItem(49201, 1);
			try {
				DollXiLianTable.get().clearDollTypes(itemobjId);//清空原来的洗练属性
				final Collection<L1DollItem> dollPowers = DollXiLianTable.get().getDollPowers();
				final List<L1DollItem> lits = new ArrayList<L1DollItem>();
				for(final L1DollItem dollItem : dollPowers){
					lits.add(dollItem);
				}
				
				//pc.sendPackets(new S_SystemMessage("shu" + lits.size()));
				Collections.shuffle(lits);
				
				int powerCount = 1;//最低1条
				final int rnd = _random.nextInt(100);
				if (rnd < Config.dollPower4Random){
					powerCount = 4;
				}else if (rnd < Config.dollPower3Random){
					powerCount = 3;
				}else if (rnd < Config.dollPower2Random){
					powerCount = 2;
				}
				final List<String> checkList = new ArrayList<String>();
				if (type.getPowerList() != null && !type.getPowerList().isEmpty()){
					for(L1DollExecutor jcece : type.getPowerList()){
						checkList.add(jcece.getClassName());
					}
				}
				for(int i = 0; i < powerCount;i++){
					final int index = _random.nextInt(lits.size());;
					final L1DollItem dollPower = lits.get(index);
					if (checkList.contains(dollPower.get_classname())){
						lits.remove(index);//移除洗练清单
						if (dollPower.get_classname().equalsIgnoreCase("Doll_BowDmgUpR")
								|| dollPower.get_classname().equalsIgnoreCase("Doll_DmgReductionR")
								|| dollPower.get_classname().equalsIgnoreCase("Doll_DmgUpR")
								|| dollPower.get_classname().equalsIgnoreCase("Doll_HprT")
								|| dollPower.get_classname().equalsIgnoreCase("Doll_MprT")
								|| dollPower.get_classname().equalsIgnoreCase("Doll_Evasion")){
							i--;
							continue;
						}
					}
					int value1 = 0;
					if (Math.abs(dollPower.get_maxValue1()) <= Math.abs(dollPower.get_minValue1())){
						value1 = dollPower.get_minValue1();
					}else{
						value1 = _random.nextInt(Math.abs(dollPower.get_maxValue1()) - Math.abs(dollPower.get_minValue1())) + Math.abs(dollPower.get_minValue1());
					}
					int value2 = 0;
					if (Math.abs(dollPower.get_maxValue2()) <= Math.abs(dollPower.get_minValue2())){
						value2 = dollPower.get_minValue2();
					}else{
						value2 = _random.nextInt(Math.abs(dollPower.get_maxValue2()) - Math.abs(dollPower.get_minValue2())) + Math.abs(dollPower.get_minValue2());
					}
					if (dollPower.get_classname().equalsIgnoreCase("Doll_Ac")){
						value1 *= -1;
					}
					final L1DollExecutor dollExe = DollXiLianTable.get().getDollType(dollPower.get_classname(), value1, value2,dollPower.get_name(),dollPower.get_nameId());
					if (dollExe != null){
						checkList.add(dollPower.get_classname());
						DollXiLianTable.get().addDollTypes(dollExe,itemobjId);
					}
				}
				checkList.clear();
				lits.clear();
				for(int j = 0; j < 10;j++){
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollxl2",item.getItem().getName(),Config.dollposs[j]));
					Thread.sleep(150);
				}
				pc.sendPackets(new S_ItemStatus(item));

				
				final String[] data = new String[5];
				data[0] = item.getItem().getName();
				final ArrayList<L1DollExecutor> lists = DollXiLianTable.get().getDollTypes(itemobjId);
				int n = 1;
				if (lists != null && !lists.isEmpty()){
					for(final L1DollExecutor exe: lists){
						if (n >= 5){
							break;
						}
						if (exe.getValue2() != 0){
							data[n] = String.format(exe.getName(),exe.getValue1(),exe.getValue2());
						}else{
							data[n] = String.format(exe.getName(),exe.getValue1());
						}
						n++;
					}
				}
				for(;n < 5;n++){
					data[n] = "空";
				}

				item.setXiLianIng(false);
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollxl1",data));
				//pc.sendPackets(new S_SystemMessage("shu" + data));
			} catch (Exception e) {
				item.setXiLianIng(false);
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
