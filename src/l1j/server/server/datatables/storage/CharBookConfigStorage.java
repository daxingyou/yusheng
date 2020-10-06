package l1j.server.server.datatables.storage;

import l1j.server.server.templates.L1BookConfig;

/** 角色记忆座标快捷键纪录设置接口 */
public interface CharBookConfigStorage {
	/** 初始化载入 */
	public void load();

	/** 传回角色 L1BookConfig */
	public L1BookConfig get(final int objectId);

	/** 新建角色 L1BookConfig */
	public void storeCharacterBookConfig(final int objectId, final byte[] data);

	/** 更新角色 L1BookConfig */
	public void updateCharacterConfig(final int objectId, final byte[] data);
}
