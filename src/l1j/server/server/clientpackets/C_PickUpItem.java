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

import l1j.server.server.ActionCodes;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;
//防洗道具 

public class C_PickUpItem extends ClientBasePacket {

	private static final String C_PICK_UP_ITEM = "[C] C_PickUpItem";

	public C_PickUpItem(byte decrypt[], LineageClient _client)
			throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int pickupCount = readD();
		//防洗道具 
		pickupCount = Math.abs(pickupCount);
		pickupCount = Math.min(pickupCount,2000000000);
		//防洗道具  end

		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}

		if (pc.isInvisble()) { // 状态
			return;
		}
		if (pc.isInvisDelay()) { // 状态
			return;
		}
		L1GroundInventory groundInventory = L1World.getInstance().getInventory(x, y,
				pc.getMapId());
		L1ItemInstance item = groundInventory.getItem(objectId);
		if (item == null){
			return;
		}
		if (item.isTradable()){
			item.setTradable(false);
		}
		if (item.getDropTimestamp() > 0){
        	final long Nowtime = (System.currentTimeMillis() - item.getDropTimestamp()) / 1000;
        	if (Nowtime < 300){
        		pc.sendPackets(new S_SystemMessage(String.format("%d秒后才可以捡取.", 300 - Nowtime)));
        		return;
        	}
        	item.setDropTimestamp(0);
        }
		if (item.getDropObjId() > 0){
			if (item.getDropObjId() != pc.getId()){
				pc.sendPackets(new S_SystemMessage("该道具不属于您."));
        		return;
			}
		}
		if (!pc.isDead()) {
			if (pc.getInventory().checkAddItem( // 容量重量确认及送信
					item, pickupCount) == L1Inventory.OK) {
				if (item.getX() != 0 && item.getY() != 0) { // 上
					if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
						return;
					}
					
					item.setDropObjId(0);
					
					groundInventory.tradeItem(item, pickupCount, pc.getInventory());

					pc.sendPackets(new S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Pickup));
					if (!pc.isGmInvis()) {
						pc.broadcastPacket(new S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Pickup));
					}
					
					if (item.getKillDeathName() != null){
						final String killName = item.getKillDeathName();
						final String mapName = MapsTable.getInstance().getMapName(pc.getMapId(),pc.getX(),pc.getY());
						item.setKillDeathName(null);
						L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4539, pc.getName(), mapName, pc.getX() + "," + pc.getY(),killName,item.getLogViewName()));
					}
					WriteLogTxt.Recording(
							"拾取记录","玩家#"+pc.getName()
							+"在地图ID"+pc.getMapId()+"X:"+x+"Y:"+y+"#玩家objid：<"+pc.getId()+">"
					        +"#拾取物品"+item.getName()+"物品objid：<"+item.getId()+">"
							+"("+pickupCount+")个。现在背包里有("+item.getCount()+")个"
							);
					//捡起打开中的灯类 
					if (item.getItemId() == 40001 || 
						item.getItemId() == 40002 || 
						item.getItemId() == 40004 || 
						item.getItemId() == 40005) {
						if (item.getEnchantLevel() != 0) {
							pc.setPcLight(item.getItem().getLightRange());
						}
						if (pc.hasSkillEffect(2)) {//日光术
							pc.setPcLight(14);
						}
			
						for(Object Light : pc.getInventory().getItems()) {
							L1ItemInstance OwnLight = (L1ItemInstance) Light;
							if((OwnLight.getItem().getItemId() == 40001 || 
							OwnLight.getItem().getItemId() == 40002 || 
							OwnLight.getItem().getItemId() == 40004 || 
							OwnLight.getItem().getItemId() == 40005) && 
							OwnLight.getEnchantLevel() != 0) {
								if(pc.getPcLight() < OwnLight.getItem().getLightRange()) {
									pc.setPcLight(OwnLight.getItem().getLightRange());
								}
							}
						}
						
						pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
						if (!pc.isInvisble() && item.getItemId() != 40004) {//非隐身中跟魔法灯笼除外
							pc.broadcastPacket(new S_Light(pc.getId(), pc.getPcLight()));
						}
					}
					//捡起打开中的灯类  end
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_PICK_UP_ITEM;
	}
}
