/*
 * 本程式是一个自由软体(Free Software)，你可以自由的散布以及、或者修改它，但是必须
 * 基于 GNU GPL(GNU General Public License) 的授权条款之下，并且随时适用于任何
 * 自由软体基金会(FSF, Free Software Foundation)所制定的最新条款。
 *
 * 这支程式的发表目的是希望将能够有用、强大，但是不附加任何的保证及担保任何责任；甚至
 * 暗示保证任何用途、方面的适销性或适用性。如果你想要了解进一步的授权内容，请详见于最
 * 新版本的 GPL 版权声明。
 *
 * 你应该会在本程式的根资料夹底下，见到适用于目前版本的 licenses.txt，这是一个复制
 * 版本的 GPL 授权，如果没有，也许你可以联系自由软体基金会取得最新的授权。
 *
 * 你可以写信到 :
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA, 02111-1307, USA.
 * 或者参观 GNU 的官方网站，以取得 GPL 的进一步资料。
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.serverpackets;

import static l1j.server.server.Opcodes.S_OPCODE_PACKETBOX;
import l1j.server.server.model.Instance.L1PetInstance;

/**
 * @author DarkNight (Kiusbt)
 * 
 * [K] 宠物属性更新封包
 */
public class S_PetAttr extends ServerBasePacket
{
	private static final int POS_PetAttr = 0x25;
	
	/**
	 * 初始化-宠物属性更新封包
	 * 
	 * @param 目标宠物 (L1PetInstance pet)
	 * @param 目标宠物之防御 (int Ac)
	 */
	public S_PetAttr(int data, L1PetInstance pet, int no)
	{
		writeC(S_OPCODE_PACKETBOX);
		writeC(POS_PetAttr); // POS
		writeC(data); // 装备框格子
		writeD(pet.getId()); // 宠物编号
		writeC(no); // 背包位置
		writeC(pet.getAc()); // 宠物防御力
	}

	/**
	 * 传回-宠物属性更新封包
	 * @see net.l1j.server.serverpackets.ServerBasePacket#getContent()
	 */
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
