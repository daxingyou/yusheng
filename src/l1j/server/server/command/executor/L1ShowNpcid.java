package l1j.server.server.command.executor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TrapInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ShowNpcid
  implements L1CommandExecutor
{
	private static final Log _log = LogFactory.getLog(L1ShowNpcid.class);


  public static L1CommandExecutor getInstance()
  {
    return new L1ShowNpcid();
  }

  public void execute(L1PcInstance pc, String cmdName, String arg)
  {
    try {
      for (L1Object object : pc.getKnownObjects()) {
        if ((object instanceof L1ItemInstance)) {
          L1ItemInstance tg = (L1ItemInstance)object;
          pc.sendPackets(new S_ChatPacket(tg, "ItemId:" + tg.getItemId(), 0));
        }
        else if ((object instanceof L1PcInstance)) {
          L1PcInstance tg = (L1PcInstance)object;
          pc.sendPackets(new S_ChatPacket(tg, "Objid:" + tg.getId(), 0));
        }
        else if ((object instanceof L1TrapInstance)) {
          L1TrapInstance tg = (L1TrapInstance)object;
          pc.sendPackets(new S_ChatPacket(object, "XY:" + tg.getX() + "/" + tg.getY(), 0));
        }
        else if ((object instanceof L1PetInstance))
        {
          pc.sendPackets(new S_ChatPacket(object, "tg: Pet", 0));
        }
        else if ((object instanceof L1SummonInstance)) {
          pc.sendPackets(new S_ChatPacket(object, "tg: Summon", 0));
        }
        else if ((object instanceof L1EffectInstance))
        {
          pc.sendPackets(new S_ChatPacket(object, "tg: Effect", 0));
        }
        else if ((object instanceof L1MonsterInstance)) {
          L1MonsterInstance tg = (L1MonsterInstance)object;
          pc.sendPackets(new S_ChatPacket(object, "NpcId:" + tg.getNpcId(), 0));
          String msg = "怪物"+tg.getNpcId()+"当前仇恨清单长度"+tg.getHateList().toTargetArrayList().size()+"AI执行状态为"+tg.isAiRunning()+"当前攻击对象为"+tg.getTarget();
          pc.sendPackets(new S_SystemMessage(msg));
        }else if (object instanceof L1DoorInstance) {
			L1DoorInstance tg = (L1DoorInstance)object;
			pc.sendPackets(new S_ChatPacket(object, "DoorId:" + tg.getDoorId(), 0));
		}
        else if ((object instanceof L1NpcInstance)) {
          L1NpcInstance tg = (L1NpcInstance)object;
          pc.sendPackets(new S_ChatPacket(object, "NpcId:" + tg.getNpcId(), 0));
          String msg = "怪物"+tg.getNpcId()+"当前仇恨清单长度"+tg.getHateList().toTargetArrayList().size()+"AI执行状态为"+tg.isAiRunning()+"当前攻击对象为"+tg.getTarget();
          pc.sendPackets(new S_SystemMessage(msg));
        }
      }
    }
    catch (Exception e)
    {
     _log.info("錯誤的 GM 指令格式: " + getClass().getSimpleName() + " 執行的GM:" + pc.getName());
      pc.sendPackets(new S_ServerMessage(261));
    }
  }
}