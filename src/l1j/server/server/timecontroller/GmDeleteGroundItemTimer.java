package l1j.server.server.timecontroller;

import java.util.ArrayList;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.world.L1World;

public class GmDeleteGroundItemTimer {
	private int _time = 0;
	private boolean _isAction = false;
	private static GmDeleteGroundItemTimer _instance;
	public static GmDeleteGroundItemTimer get(){
		if (_instance == null){
			_instance = new GmDeleteGroundItemTimer();
		}
		return _instance;
	}
	public GmDeleteGroundItemTimer() {
		
	}
	
	public void start(final int t){
		if (_isAction){
			return;
		}
		_isAction = true;
		_time =  t;
		L1World.getInstance().broadcastServerMessage(String.format("地上的垃圾将在%d秒后被清除", _time));
		DeleteTimer ub = new DeleteTimer();
		GeneralThreadPool.getInstance().execute(ub);
	}
	
	public boolean isAction(){
		return _isAction;
	}

	class DeleteTimer implements Runnable {
		@Override
		public void run() {
			for(int round = _time;round > 0;round --){
				if (round <= 10){
					L1World.getInstance().broadcastServerMessage(String.format("地上的垃圾将在%d秒后被清除",round));
				}
				try {
					Thread.sleep(1000);
				} catch (Exception exception) {
					break;
				}
			}
			deleteItem();
			_isAction = false;
			L1World.getInstance().broadcastServerMessage("地上的垃圾被GM清除了。");
		}
		
		private void deleteItem() {
			for (L1Object l1object : L1World.getInstance().getObject()) {
				if (l1object instanceof L1ItemInstance) {
					L1ItemInstance l1iteminstance = (L1ItemInstance) l1object;
					if (l1iteminstance.getX() == 0 && l1iteminstance.getY() == 0) { // 地面上のアイテムではなく、谁かの所有物
						continue;
					}

					ArrayList<L1PcInstance> players = L1World.getInstance()
							.getVisiblePlayer(l1iteminstance, 0);
					if (0 == players.size()) {
						L1Inventory groundInventory = L1World.getInstance()
								.getInventory(l1iteminstance.getX(),
										l1iteminstance.getY(),
										l1iteminstance.getMapId());
						int itemId = l1iteminstance.getItem().getItemId();
						if (itemId == 40314 || itemId == 40316) { // ペットのアミュレット
							PetTable.getInstance()
									.deletePet(l1iteminstance.getId());
						} else if (itemId >= 49016 && itemId <= 49025) { // 便笺
							LetterTable lettertable = new LetterTable();
							lettertable.deleteLetter(l1iteminstance.getId());
						} else if (itemId >= 41383 && itemId <= 41400) { // 家具
							if (l1object instanceof L1FurnitureInstance) {
								L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
								if (furniture.getItemObjId() == l1iteminstance
										.getId()) { // 既に引き出している家具
									FurnitureSpawnTable.getInstance()
											.deleteFurniture(furniture);
								}
							}
						}
						groundInventory.removeItem(l1iteminstance);
						L1World.getInstance().removeVisibleObject(l1iteminstance);
						L1World.getInstance().removeWorldObject(l1iteminstance);
					}
				}
			}
		}
	}
}
