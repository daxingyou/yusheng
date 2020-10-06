package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.datatables.CenterTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.IPCountTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ChangeSever implements L1CommandExecutor {
	private static final Log _log = LogFactory.getLog(L1ChangeSever.class);


	private L1ChangeSever() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeSever();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			final StringTokenizer stringtokenizer = new StringTokenizer(arg);
			final int mode = Integer.parseInt(stringtokenizer.nextToken());
			switch (mode) {
			case 1:
				DropTable.getInstance().reload();
				pc.sendPackets(new S_SystemMessage("掉落物重讀完畢。"));
				break;
			case 2:
				ShopTable.getInstance().reload();;
				pc.sendPackets(new S_SystemMessage("商店資訊重讀完畢。"));
				break;
			case 3:
				CenterTable.getInstance().reload();;
				pc.sendPackets(new S_SystemMessage("商城商店資訊重讀完畢。"));
				break;
			case 5:
				Dungeon.getInstance().reload();
				pc.sendPackets(new S_SystemMessage("座標資訊重讀完畢。"));
				break;
			case 7:
				SkillsTable.getInstance().reload();
				pc.sendPackets(new S_SystemMessage("技能表資訊重讀完畢。"));
				break;
			case 8:
				MobSkillTable.getInstance().reload();
				pc.sendPackets(new S_SystemMessage("怪物技能表資訊重讀完畢。"));
				break;
			case 10:
				GetBackRestartTable.getInstance().reload();
				pc.sendPackets(new S_SystemMessage("回卷傳送座標表重讀完畢。"));
				break;
			case 11:
				IPCountTable.get().reload();
				pc.sendPackets(new S_SystemMessage("回卷傳送座標表重讀完畢。"));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			_log.info("錯誤的 GM 指令格式: " + this.getClass().getSimpleName()
					+ " 執行 GM :" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_SystemMessage("【請輸入欲動態更新的資料值】：\n\r"
					+ "01:怪物掉落\n\r" + "02:商店資訊\n\r" + "03:商城資訊\n\r"
					 + "05:傳送洞口\n\r" 
					+ "07:技能資訊\n\r" + "08:怪物技能\n\r"
					+ "10:回卷座標\n\r"));
		}
	}
}
