/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.clientpackets;

import java.text.SimpleDateFormat;
import java.util.Date;

import l1j.server.Config;
//import l1j.server.server.ClientThread;
import l1j.server.server.ServerRestartTimer;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage; // 线上资讯 
import l1j.server.server.serverpackets.S_WhoAmount;
import l1j.server.server.timecontroller.WorldCalcExp;
import l1j.server.server.world.L1World;

//import l1j.william.L1GameReStart; // 伺服器自动重启 

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Who extends ClientBasePacket {

	private static final String C_WHO = "[C] C_Who";
	
	public C_Who(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		String s = readS();
		L1PcInstance target_pc = L1World.getInstance().getPlayer(s);
		L1PcInstance pc = _client.getActiveChar();
		if (target_pc!= null) {
			if (!pc.getInventory().checkItem(40308, 500)){
				pc.sendPackets(new S_SystemMessage("\\F2偷窥一次需要金币500."));
				return;
			}
			final String[] msg = new String[17];
			pc.getInventory().consumeItem(40308, 500);
			msg[0] = target_pc.getName();
			msg[1] = String.valueOf(target_pc.getLevel());
			msg[2] = String.valueOf(target_pc.getCurrentHp()) + " / " + String.valueOf(target_pc.getMaxHp());
			msg[3] = String.valueOf(target_pc.getCurrentMp()) + " / " + String.valueOf(target_pc.getMaxMp());
			msg[4] = String.valueOf(target_pc.getStr());
			msg[5] = String.valueOf(target_pc.getCon());
			msg[6] = String.valueOf(target_pc.getDex());
			msg[7] = String.valueOf(target_pc.getWis());
			msg[8] = String.valueOf(target_pc.getInt());
			msg[9] = String.valueOf(target_pc.getCha());
			msg[10] = String.valueOf(target_pc.getAc());
			msg[11] = String.valueOf(target_pc.getEr());
			msg[12] = String.valueOf(target_pc.getMr()) + " %";
			msg[13] = String.valueOf(target_pc.getFire()) + " %";
			msg[14] = String.valueOf(target_pc.getWater()) + " %";
			msg[15] = String.valueOf(target_pc.getWind()) + " %";
			msg[16] = String.valueOf(target_pc.getEarth()) + " %";
			pc.setTouKuiName(target_pc.getName());
			pc.set_tuokui_objId(target_pc.getId());
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "toukuipc", msg));
		} else {
			if (Config.ALT_WHO_COMMAND) {
				final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
				switch (Config.ALT_WHO_TYPE) {
				case 0:
//					String amount = String.valueOf(L1World.getInstance()
//							.getAllPlayers().size());
					final int count = L1World.getInstance().getAllPlayers().size();
					final String amount = String.valueOf((int) ( 50 + (count) * 1.5));
					//删除S_WhoAmount s_whoamount = new S_WhoAmount(amount);
					//删除pc.sendPackets(s_whoamount);
					// 线上资讯 
					S_WhoAmount s_whoamount = new S_WhoAmount(amount);
	                pc.sendPackets(s_whoamount);
	             //   pc.sendPackets(new S_SystemMessage("----------------------------------------"));
	                
	                int i=1;
	                for (L1PcInstance pc1 : L1World.getInstance().getAllPlayers()) {
						if (pc.isGm() == true) {
	                   //     pc.sendPackets(new S_SystemMessage(i + ". 玩家:{ " + pc1.getName() + " }、血盟:{ " + pc1.getClanname() + " }、等级:{ " + pc1.getLevel() + " }"));
	                 	} else {
	                    //    pc.sendPackets(new S_SystemMessage(i + ". 玩家:{ " + pc1.getName() + " }、血盟:{ " + pc1.getClanname() + " }"));	                 		
	                   	}
							
	                  //  i++;
	                }
	                
	                pc.sendPackets(new S_SystemMessage("----------------------------------------"));
	                pc.sendPackets(new S_SystemMessage("当前在线玩家： " + amount + "人"));
	                if (WorldCalcExp.get().isRuning()){
	                	pc.sendPackets(new S_SystemMessage("经验值: " + Config.RATE_XP + " 倍 x2 剩余时间:"  + WorldCalcExp.get().getTime() + "秒"));
	                }else{
	                	pc.sendPackets(new S_SystemMessage("经验值: " + Config.RATE_XP + " 倍"));
	                }
					pc.sendPackets(new S_SystemMessage("正义值: " + Config.RATE_LA + " 倍"));
					pc.sendPackets(new S_SystemMessage("友好度: " + Config.RATE_KARMA + " 倍"));
					pc.sendPackets(new S_SystemMessage("负重率: " + Config.RATE_WEIGHT_LIMIT + " 倍"));
					pc.sendPackets(new S_SystemMessage("掉宝率: 2 倍"));
					pc.sendPackets(new S_SystemMessage("取得金币: 2 倍"));
					pc.sendPackets(new S_SystemMessage("宠物经验值: " + Config.PET_RATE_XP + " 倍"));
					pc.sendPackets(new S_SystemMessage("宠物等级上限: " + Config.PET_LEVEL + " 级"));
					pc.sendPackets(new S_SystemMessage("冲装率: 武器 "+Config.ENCHANT_CHANCE_WEAPON+"%  /  防具 "+Config.ENCHANT_CHANCE_ARMOR+"%"));
					pc.sendPackets(new S_SystemMessage("重启时间: "+ ServerRestartTimer.get_restartTime()));
					/*if (Config.REST_TIME != 0) {
	                	int second = L1GameReStart.getInstance().GetRemnant();
						pc.sendPackets( new S_SystemMessage("距离伺服器重启还有: " + ( second / 60 ) / 60 + " 小时 " 
							+ ( second / 60 ) % 60 + " 分 " + second % 60 + " 秒。"));
	                }*/
	                pc.sendPackets(new S_SystemMessage("----------------------------------------"));
	                // 线上资讯  end
					break;
				case 1:
					final String[] info = new String[]{
							Config.SERVERNAME,// 伺服器资讯:
							String.valueOf(Config.RATE_XP ),// 经验
							String.valueOf(Config.RATE_DROP_ITEMS),// 掉宝
							String.valueOf(Config.RATE_DROP_ADENA),// 金币
							String.valueOf(Config.RATE_LA),// 正义
							String.valueOf(Config.RATE_WEIGHT_LIMIT),// 负重
							String.valueOf(Config.ENCHANT_CHANCE_WEAPON),// 武器
							String.valueOf(Config.ENCHANT_CHANCE_ARMOR),// 防具
							
							String.valueOf(Config.BONUS_STATS1),// 手点上限
							String.valueOf(Config.BONUS_STATS3),// 单项万能药上限
							String.valueOf(Config.BONUS_STATS2),// 总和万能药瓶数
							nowDate,// 目前时间
							ServerRestartTimer.get_restartTime()// 重启时间
					};
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_who", info));
					break;
				}
				
			} else {
				String amount = String.valueOf(L1World.getInstance()
						.getAllPlayers().size());
				S_WhoAmount s_whoamount = new S_WhoAmount(amount);
                pc.sendPackets(s_whoamount);
			}
			// 对像居场合表示？方修正愿。
			WriteLogTxt.Recording(
					"查询人数记录","玩家#"+pc.getName()
					+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
			        +"查询人数。"
					);
		}
	}

	@Override
	public String getType() {
		return C_WHO;
	}
}
