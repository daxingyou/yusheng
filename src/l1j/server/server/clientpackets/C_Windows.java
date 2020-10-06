package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.lock.CharBookReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBoxLoc;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 視窗失焦
 * 
 * @author dexc
 * 
 */
public final class C_Windows extends ClientBasePacket {
	
	private static final Log _log = LogFactory.getLog(C_Windows.class);


	public C_Windows(byte[] decrypt, LineageClient client) {
		super(decrypt);
		try {
			// 資料載入
			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) { // 角色為空
				return;
			}
			int type = readC();
			//System.out.println(PacketHandler.printData(decrypt, decrypt.length));
			switch (type) {
			case 0x00:
				int objid = readD();
				L1Object obj = L1World.getInstance().findObject(objid);
				if (obj instanceof L1PcInstance) {
					L1PcInstance tgpc = (L1PcInstance) obj;
					_log.warn("玩家:" + pc.getName() + " 申诉:(" + objid + ")"
							+ tgpc.getName());
				} else {
					_log.warn("玩家:" + pc.getName() + " 申诉:NPC(" + objid + ")");
				}
				break;
            case 13: // 窗口失焦(每次切换都会发)
                break;
            case 32: // 打开仓库
                break;
            case 34: // 保存记忆坐标(按下保存按钮或直接关闭窗口)
                break;
            case 39: // 修改记忆坐标(调整顺序、更改颜色等等)
            	// 變更的座標項目數量
				final int changeCount = this.readD();
				for (int i = 0; i < changeCount; i++) {
					final int bookId = this.readD();
					final String changeName = this.readS();
					final L1BookMark bm = CharBookReading.get().getBookMark(pc, bookId);
					if (bm != null){
						if (bm.getName().equalsIgnoreCase(changeName)) {
							// 同樣的名稱已經存在。
							pc.sendPackets(new S_ServerMessage(327));
							continue;
						}
						changeBookmarkName(pc, bookId,changeName);
					}
				}
            	break;
            case 0x0b:
                String name = readS();
                int mapid = readH();
                int x = readH();
                int y = readH();
                int zone = readD();
                L1PcInstance target = L1World.getInstance().getPlayer(name);
                if (target != null) {
                    target.sendPackets(new S_PacketBoxLoc(pc.getName(),
                            mapid, x, y, zone));
                    pc.sendPackets(new S_ServerMessage(1783, name));// 已发送座标位置给%0。

                } else {
                    pc.sendPackets(new S_ServerMessage(1782));// 无法找到该角色或角色不在线上。
                }
                break;
            case 0x2c: // 清空杀怪数量
                pc.setKillMonCount(0);
                pc.sendPackets(new S_OwnCharStatus(pc));
                break;
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			this.over();
		}
	}
	
	public void changeBookmarkName(final L1PcInstance pc, final int bookid,
			final String s) {
		final ArrayList<L1BookMark> bookmarks = CharBookReading.get().getBookMarks(pc);

		if (bookmarks != null) {
			for (final L1BookMark book : bookmarks) {
				if (book.getId() == bookid) {
					// 設置新座標名稱
					book.setName(s);

					// 更新數據庫
					Connection cn = null;
					PreparedStatement ps = null;

					try {
						cn = L1DatabaseFactory.getInstance().getConnection();
						ps = cn.prepareStatement("UPDATE `character_teleport` SET `name`=? WHERE `id`=?");
						ps.setString(1, book.getName());
						ps.setInt(2, book.getId());
						ps.execute();
					} catch (final SQLException e) {
						_log.error(e.getLocalizedMessage(), e);
					} finally {
						SQLUtil.close(ps);
						SQLUtil.close(cn);
					}
					break;
				}
			}
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
