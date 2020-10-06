package l1j.server.data.npc.power;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.TuiguangTable;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1TuiGuang;
import l1j.server.server.utils.SQLUtil;
import l1j.william.Reward;

public class Npc_Huigui extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Huigui.class);
	
	private static final CopyOnWriteArrayList<String>_tuiguangs = new CopyOnWriteArrayList<String>();

	/**
	 * 精仿服务
	 */
	private Npc_Huigui() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Huigui();
	}

	@Override
	public int type() {
		return 3;
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		String[] htmldata = new String[] { npc.getName()+
				":你好！亲爱的冒险者，我是"+npc.getName()+"，我一直在等着你，欢迎您的到来！感谢您的一直支持！",
				 " ", "我是来领推广奖励的！", "", "","",""," "," "," ",
				 " "," "," "," "," "," "," "
};
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "a2_1",htmldata));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("a2_1_1")) {
			if (pc.getLevel() > 47) {
				if (isok(pc.getAccountName())) {
					if (!pc.getQuest().isEnd(L1Quest.QUEST_HUIGUI)) {
						L1ItemInstance item = pc.getInventory().storeItem(60209, 1);
						if (item != null) {
							pc.getQuest().set_end(L1Quest.QUEST_HUIGUI);
							pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
									.get_name(), item.getLogName())); // \f1%0が%1をくれました。
						}
					}else {
						pc.sendPackets(new S_SystemMessage("您已经领过奖励！"));
					}
				}else {
					pc.sendPackets(new S_SystemMessage("抱歉，您的条件未满足！"));
				}		
			}
			else {
				pc.sendPackets(new S_SystemMessage("抱歉，您当前角色等级未到48级！"));
			}
		}else if (cmd.equalsIgnoreCase("a2_1_2")) {
			if (_tuiguangs.contains(pc.getAccountName())) {
				pc.sendPackets(new S_SystemMessage("您今天已经领过奖励，请明天再来！"));
				return;
			}
			L1TuiGuang tuiGuang  = TuiguangTable.getInstance().isok(pc.getAccountName());
			if (tuiGuang != null) {			
				L1ItemInstance item = ItemTable.getInstance().createItem(tuiGuang.getTitemid());
//				System.out.println("发送奖励信息："+tuiGuang.getTitemid()+","+tuiGuang.getTcount());
				if (item != null) {
					item.setCount(tuiGuang.getTcount());
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
							.get_name(), item.getLogName())); // \f1%0が%1をくれました。
					_tuiguangs.add(tuiGuang.getTaccount());
					WriteLogTxt.Recording("推广领取记录", "玩家 "+pc.getName()+" OBJID:"+pc.getId()+"领取"+item.getLogViewName());
				}
			}else {
				pc.sendPackets(new S_SystemMessage("您没有推广，或者奖励会有延迟请稍后再来！"));
			}
		}
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), ""));	
	}
	
	private  boolean isok(final String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT * FROM accounts_copy WHERE login=? LIMIT 1";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				return false;
			}		
			Timestamp lastActive = rs.getTimestamp("lastactive");
			
			Date date = new Date();
			
			long time = date.getTime() - lastActive.getTime();
			time = time / 1000 / 60 / 60 / 24;
			if (time > 15) {
				return true;
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return false;
	}
}
