package l1j.server.server.model;

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.ActionCodes;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TradeAddItem;
import l1j.server.server.serverpackets.S_TradeStatus;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 玩家相互交易判斷類
 * 
 * @author dexc
 * 
 */
public class L1Trade {

	private static final Log _log = LogFactory.getLog(L1Trade.class);
    /**
	 * 加入交易物品
	 * 
	 * @param pc
	 * @param itemid
	 * @param itemcount
	 */
	public void tradeAddItem(final L1PcInstance pc, final int itemObjid,long itemcount) {
		L1PcInstance trading_partner = null;
		try {
			// 取回要加入交易的物品
			final L1ItemInstance item = pc.getInventory().getItem(itemObjid);
			if (item == null) {
				return;
			}
			final L1Object object = L1World.getInstance().findObject(pc.getTradeID());
			if (object instanceof L1NpcInstance) {
				pc.getInventory().tradeItem(item, itemcount, pc.getTradeWindowInventory());
				pc.sendPackets(new S_TradeAddItem(item, itemcount, 0));
				return;
			}
			// 取回交易對象
			trading_partner = (L1PcInstance) L1World.getInstance().findObject(pc.getTradeID());

			if (trading_partner == null) {
				return;
			}

			if (item.isEquipped()) {
				return;
			}
			itemcount = Math.max(0, itemcount);
			// 檢查數量
			final boolean checkItem = pc.getInventory().checkItem(item.getItemId(), itemcount);
			if (checkItem) {
				if (item.getItemCharaterTrade() != null){
					final ArrayList<String> datas = pc.getNetConnection().getAccount().loadCharacterItems(item.getItemCharaterTrade().get_char_objId());
					trading_partner.sendPackets(new S_SystemMessage("\\F1以下是<" + item.getItemCharaterTrade().getName() + ">背包数据."));
					trading_partner.sendPackets(new S_SystemMessage("-------------------------------"));
					for(final String names : datas){
						trading_partner.sendPackets(new S_SystemMessage(names));
					}
					trading_partner.sendPackets(new S_SystemMessage(String.format("\\F1职业:[%s] 等级:%d",L1PCAction.TYPE_CLASS[item.getItemCharaterTrade().get_Type()],item.getItemCharaterTrade().getLevel())));
					trading_partner.sendPackets(new S_SystemMessage("-------------------------------"));
				}
				WriteLogTxt.Recording("交易记录", "玩家 "+pc.getName()+" 往玩家"+trading_partner.getName()+"交易框加入"+item.getNumberedLogViewName()+"("+itemcount+")"+" OBJID: "+item.getId()+" ");
				pc.getInventory().tradeItem(item, itemcount, pc.getTradeWindowInventory());
				pc.sendPackets(new S_TradeAddItem(item, itemcount, 0));
				trading_partner.sendPackets(new S_TradeAddItem(item, itemcount,1));
			} else {
				pc.sendPackets(new S_TradeStatus(1));
				trading_partner.sendPackets(new S_TradeStatus(1));

				pc.setTradeOk(false);
				trading_partner.setTradeOk(false);

				pc.setTradeID(0);
				trading_partner.setTradeID(0);
				return;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {

		}
	}

	/**
	 * 交易完成
	 * 
	 * @param pc
	 */
	public void tradeOK(final L1PcInstance pc) {
		final L1Object object = L1World.getInstance().findObject(
				pc.getTradeID());
		if (object instanceof L1NpcInstance) {
			if (pc.getTradeWindowInventory().checkItem(40308, 10000)){
				pc.getTradeWindowInventory().consumeItem(40308, 10000);
				List<L1ItemInstance> player_tradelist = pc.getTradeWindowInventory().getItems();
				int player_tradecount = pc.getTradeWindowInventory().getSize();
				for (int cnt = 0; cnt < player_tradecount; cnt++) {
					final L1ItemInstance item = (L1ItemInstance) player_tradelist.get(0);
					pc.getTradeWindowInventory().tradeItem(item,item.getCount(),pc.getInventory());
				}
				pc.sendPackets(new S_TradeStatus(0));
				pc.setTradeOk(false);
				pc.setTradeID(0);
				int[] skillIds = new int[]{19,26,42,43,48,151,158};
				
				for(final L1NpcInstance npc : L1World.getInstance().getVisibleNpc(pc, 10)){
					if (npc.getNpcId() >= 8895 && npc.getNpcId() <= 8898){
						npc.broadcastPacket(new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_SkillBuff));
					}
				}
				
				for(int i = 0;i<skillIds.length;i++){
					new L1SkillUse().handleCommands(pc, skillIds[i],pc.getId(), pc.getX(), pc.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
				}
			}else{
				tradeCancel(pc);
			}
			return;
		}
		L1PcInstance trading_partner = null;
		int cnt = 0;
		// 交易物品暫存
		try {
			// 取回交易對象
			trading_partner = (L1PcInstance) L1World.getInstance().findObject(
					pc.getTradeID());
			if (trading_partner != null) {
				List<L1ItemInstance> player_tradelist = pc
						.getTradeWindowInventory().getItems();
				int player_tradecount = pc.getTradeWindowInventory().getSize();

				List<L1ItemInstance> trading_partner_tradelist = trading_partner
						.getTradeWindowInventory().getItems();
				int trading_partner_tradecount = trading_partner
						.getTradeWindowInventory().getSize();
				for (cnt = 0; cnt < player_tradecount; cnt++) {
					L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist
							.get(0);
					if (l1iteminstance1.isTradable()){
						l1iteminstance1.setTradable(false);//交易成功后 更新不可交易
					}
					pc.getTradeWindowInventory().tradeItem(l1iteminstance1,
							l1iteminstance1.getCount(),
							trading_partner.getInventory());
					WriteLogTxt.Recording("交易记录", "玩家#" + pc.getName()
							+ "#玩家objid：<" + pc.getId() + ">" + "#转移物品"
							+ l1iteminstance1.getNumberedLogViewName() + "("
							+ l1iteminstance1.getCount() + ")" + "物品objid：<"
							+ l1iteminstance1.getId() + ">" + "到交易对象#"
							+ trading_partner.getName() + "#交易对象objid：<"
							+ trading_partner.getId() + ">" + "#");
				}
				for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
					L1ItemInstance l1iteminstance2 = (L1ItemInstance) trading_partner_tradelist
							.get(0);
					if (l1iteminstance2.isTradable()){
						l1iteminstance2.setTradable(false);//交易成功后 更新不可交易
					}
					trading_partner.getTradeWindowInventory().tradeItem(
							l1iteminstance2, l1iteminstance2.getCount(),
							pc.getInventory());

					WriteLogTxt.Recording("交易记录",
							"玩家#" + trading_partner.getName() + "#玩家objid：<"
									+ trading_partner.getId() + ">" + "#转移物品"
									+ l1iteminstance2.getNumberedLogViewName()
									+ "(" + l1iteminstance2.getCount() + ")"
									+ "物品objid：<" + l1iteminstance2.getId()
									+ ">" + "到交易对象#" + pc.getName()
									+ "#交易对象objid：<" + pc.getId() + ">");
				}
				pc.sendPackets(new S_TradeStatus(0));
				trading_partner.sendPackets(new S_TradeStatus(0));

				pc.setTradeOk(false);
				trading_partner.setTradeOk(false);

				pc.setTradeID(0);
				trading_partner.setTradeID(0);
			} else {
				tradeCancel(pc);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {

		}
	}

	/**
	 * 交易取消
	 * 
	 * @param pc
	 */
	public void tradeCancel(final L1PcInstance pc) {
		final L1Object object = L1World.getInstance().findObject(pc.getTradeID());
		if (object instanceof L1NpcInstance) {
			// 取回自己的交易物品
			List<L1ItemInstance> player_tradelist = pc.getTradeWindowInventory().getItems();
			int player_tradecount = pc.getTradeWindowInventory().getSize();
			for (int cnt = 0; cnt < player_tradecount; cnt++) {
				L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist
						.get(0);
				pc.getTradeWindowInventory().tradeItem(l1iteminstance1,
						l1iteminstance1.getCount(), pc.getInventory());
			}
			pc.sendPackets(new S_TradeStatus(1));
			pc.setTradeOk(false);
			pc.setTradeID(0);
			return;
		}
		long jinbincount = 0;
		L1ItemInstance itemInstance = pc.getInventory().findItemId(L1ItemId.ADENA);
		if (itemInstance != null) {
			jinbincount = itemInstance.getCount();
		}
		WriteLogTxt.Recording("交易记录", "玩家 "+pc.getName()+" 开始取消交易,当前身上金币总数为: "+jinbincount+" ！");
		L1PcInstance trading_partner = null;
		try {
			trading_partner = (L1PcInstance) L1World.getInstance().findObject(pc.getTradeID());
			int cnt = 0;
			if (trading_partner != null) {
				WriteLogTxt.Recording("交易记录", "玩家 "+pc.getName()+" 取消和 "+trading_partner.getName()+" 交易处理中！");
				// 取回自己的交易物品
    			List<L1ItemInstance> player_tradelist = pc.getTradeWindowInventory().getItems();
    			int player_tradecount = pc.getTradeWindowInventory().getSize();

    			List<L1ItemInstance> trading_partner_tradelist = trading_partner
    					.getTradeWindowInventory().getItems();
    			int trading_partner_tradecount = trading_partner
    					.getTradeWindowInventory().getSize();

    			for (cnt = 0; cnt < player_tradecount; cnt++) {
    				L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist
    						.get(0);
    				pc.getTradeWindowInventory().tradeItem(l1iteminstance1,
    						l1iteminstance1.getCount(), pc.getInventory());
    			}
    			for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
    				L1ItemInstance l1iteminstance2 = (L1ItemInstance) trading_partner_tradelist
    						.get(0);
    				trading_partner.getTradeWindowInventory().tradeItem(
    						l1iteminstance2, l1iteminstance2.getCount(),
    						trading_partner.getInventory());
    			}
    			pc.sendPackets(new S_TradeStatus(1));
				pc.setTradeOk(false);
				pc.setTradeID(0);
				
				trading_partner.sendPackets(new S_TradeStatus(1));			
				trading_partner.setTradeOk(false);			
				trading_partner.setTradeID(0);
				
				L1ItemInstance itemInstance3 = pc.getInventory().findItemId(L1ItemId.ADENA);
				if (itemInstance3 != null) {
					jinbincount = itemInstance3.getCount();
				}else {
					jinbincount = 0;
				}
				WriteLogTxt.Recording("交易记录", "玩家 "+pc.getName()+" 取消和 "+trading_partner.getName()+" 交易处理完毕，当前身上金币总数为: "+jinbincount+" ！");
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			
		}
	}
}
