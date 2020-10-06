package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.templates.L1Skills;

public class Item_Skill extends ItemExecutor {

		/**
		 *
		 */
		private Item_Skill() {
			// TODO Auto-generated constructor stub
		}

		public static ItemExecutor get() {
			return new Item_Skill();
		}

		/**
		 * 道具物件執行
		 * 
		 * @param data
		 *            參數
		 * @param pc
		 *            執行者
		 * @param item
		 *            物件
		 */
		@Override
		public void execute(final int[] data, final L1PcInstance pc,
				final L1ItemInstance item) {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}
			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}
			
			if (_skillId != null){
				pc.getInventory().removeItem(item, 1);
				for(int i = 0;i<_skillId.length;i++){
					final L1Skills skill = SkillsTable.getInstance().getTemplate(_skillId[i]);
					new L1SkillUse().handleCommands(pc,_skillId[i],pc.getId(), pc.getX(), pc.getY(), null,skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
				}
			}
		}
		
		private int[] _skillId;
		public void set_set(String[] set) {
			if (set.length > 1){
				_skillId = new int[set.length-1];
				for(int i = 1;i<set.length;i++){
					_skillId[i-1] = Integer.parseInt(set[i]);
				}
			}
		}
}
