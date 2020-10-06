package l1j.server.server.command.executor;

import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.datatables.AuctionBoardTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1AuctionBoard;
import l1j.server.server.world.L1World;


/**
 * GM指令：線上修改盟屋結標時間 usage：.endauction 10 (血盟小屋將在 10 分鐘後結標。)
 * Author：a6572517@yahoo.com.tw
 */

public class L1ChangeBoardAuction implements L1CommandExecutor {
	private L1ChangeBoardAuction() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeBoardAuction();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			Calendar DeadTime = getRealTime();
			DeadTime.add(Calendar.MINUTE, Integer.parseInt(arg));
			AuctionBoardTable boardTable = new AuctionBoardTable();
			for (L1AuctionBoard board : boardTable.getAuctionBoardTableList()) {
				board.setDeadline(DeadTime);
				boardTable.updateAuctionBoard(board);
			}

			L1World.getInstance().broadcastPacketToAll(
					new S_SystemMessage("血盟小屋" + arg + "分钟后结标，需要购买的请速度下标。"));

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("请输入 " + cmdName + " 分钟数"));
		}
	}

	private Calendar getRealTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		return cal;
	}
}
