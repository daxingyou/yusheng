package l1j.server.server.model.Instance;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.DollXiLianTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.doll.Doll_Add_Skill;
import l1j.server.server.model.doll.Doll_HprT;
import l1j.server.server.model.doll.Doll_MprT;
import l1j.server.server.model.doll.L1DollExecutor;
import l1j.server.server.serverpackets.S_BabyPack;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Doll;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.world.L1World;

public class L1BabyInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private ScheduledFuture<?> _summonFuture;
	private int _currentPetStatus;
	private boolean _tamed;
	private static Random _random = new Random();
	
	private final L1Doll _type;
	private int _hprTime = 0;
	private int _hpr = 0;
	private long _oldhprTime = 0;
	private int _mprTime = 0;
	private int _mpr = 0;
	private long _oldmprTime = 0;
	
	private ArrayList<L1DollExecutor> powerList_xl = null;

	// 场合处理
	@Override
	public boolean noTarget() {
		//主人隐身或解除则魔法娃娃消失 
		if(_master != null && _master.isInvisble() || _currentPetStatus == 6) {
			Death(null);
		}
		if (_master != null){
			if (_hprTime > 0 && _hpr > 0){
				if (_oldhprTime == 0){
					_oldhprTime = System.currentTimeMillis();
				}else{
					if (System.currentTimeMillis() - _oldhprTime >= _hprTime * 1000){
						_oldhprTime = System.currentTimeMillis();
						broadcastPacket(new S_SkillSound(_master.getId(), 6321));
						if (_master.getCurrentHp() + _hpr >= _master.getMaxHp()) {
							_master.setCurrentHp(_master.getMaxHp());
						} else {
							_master.setCurrentHp(_master.getCurrentHp() + _hpr);
						}
					}
				}
			}
			if (_mprTime > 0 && _mpr > 0){
				if (_oldmprTime == 0){
					_oldmprTime = System.currentTimeMillis();
				}else{
					if (System.currentTimeMillis() - _oldmprTime >= _mprTime * 1000){
						_oldmprTime = System.currentTimeMillis();
						broadcastPacket(new S_SkillSound(_master.getId(), 6321));
						if (_master.getCurrentMp() + _mpr >= _master.getMaxMp()) {
							_master.setCurrentMp(_master.getMaxMp());
						} else {
							_master.setCurrentMp(_master.getCurrentMp() + _mpr);
						}
					}
				}
			}
		}
		if (_master != null && _master.getMapId() == getMapId()) {
			// ●主人追尾
			final int range = getLocation().getTileLineDistance(_master.getLocation());
			if (range > 2 && range < 15) {
				int dir = moveDirection(_master.getX(), _master.getY());
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			} else if (range >= 15) {
				//强制移动身边
				this.nearTeleport(_master.getX(),_master.getY());
				setSleepTime(calcSleepTime(getPassispeed()));
			} else if ((_random.nextInt(100) + 1) >= 96) {
				if ((_random.nextInt(100) + 1) < 61) {
					broadcastPacket(new S_DoActionGFX(getId(),66));
					setSleepTime(calcSleepTime(getNpcTemplate().getAtkMagicSpeed()));
				} else {
					broadcastPacket(new S_DoActionGFX(getId(),67));
					setSleepTime(calcSleepTime(getNpcTemplate().getSubMagicSpeed()));
				}
			}
		}
		return false;
	}

	// １时间计测用
	class SummonTimer implements Runnable {
		@Override
		public void run() {
			if (_destroyed) { // 既破弃
				return;
			}
			// 解散
			Death(null);
		}
	}

	// 用
	public L1BabyInstance(final L1Npc template, final L1PcInstance master,final L1Doll type,final int itemObjId) {
		super(template);
		_type = type;
		setId(IdFactory.getInstance().nextId());
		setGfxId(type.get_gfxid());
        setTempCharGfx(type.get_gfxid());
        setNameId(type.get_nameid());
		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());
		setItemObjId(itemObjId);
		
		_currentPetStatus = 3;
		_tamed = false;
		
		 // 设置能力
        if (!_type.getPowerList().isEmpty()) {
        	final StringBuilder msg1 = new StringBuilder();
        	msg1.append("\\F3基础属性1:");
            for (L1DollExecutor p : _type.getPowerList()) {
            	if (p instanceof Doll_HprT){
            		_hprTime = p.getValue1();
            		_hpr = p.getValue2();
            	}else if (p instanceof Doll_MprT){
            		_mprTime = p.getValue1();
            		_mpr = p.getValue2();
            	}else {
            		p.setDoll(master);
            	}
            	if(p instanceof Doll_Add_Skill){
                	final L1Skills skills = SkillsTable.getInstance().getTemplate(p.getValue1());
                	msg1.append(String.format(p.getName(), skills.getName()));
                }else if(p.getValue2() != 0){
                	msg1.append(String.format(p.getName(), p.getValue1(), p.getValue2()));
                }else{
                	msg1.append(String.format(p.getName(), p.getValue1()));
                }
            	msg1.append(" ");
            }
            master.sendPackets(new S_SystemMessage(msg1.toString()));
        }
        
        final ArrayList<L1DollExecutor> dollExecutors = DollXiLianTable.get().getDollTypes(itemObjId);
        if (dollExecutors != null && !dollExecutors.isEmpty()){
        	final StringBuilder msg = new StringBuilder();
        	msg.append("\\F3洗练属性1:");
        	for (final L1DollExecutor pxl : dollExecutors) {
            	if (pxl instanceof Doll_HprT){
            		_hprTime = pxl.getValue1();
            		_hpr = pxl.getValue2();
            	}else if (pxl instanceof Doll_MprT){
            		_mprTime = pxl.getValue1();
            		_mpr = pxl.getValue2();
            	}else {
            		pxl.setDoll(master);
            	}
            	if (pxl.getValue2() != 0){
            		msg.append(String.format(pxl.getName(), pxl.getValue1(), pxl.getValue2()));
            	}else{
            		msg.append(String.format(pxl.getName(), pxl.getValue1()));
            	}
            	msg.append(" ");
            }
        	master.sendPackets(new S_SystemMessage(msg.toString()));
        }
        
		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
		
		_summonFuture = GeneralThreadPool.getInstance().schedule(new SummonTimer(), type.get_time() * 1000);
		master.sendPackets(new S_PacketBox(S_PacketBox.DOLL_ICON,type.get_time())); // 魔法娃娃状态图示
		broadcastPacket(new S_SkillSound(getId(), 5935));
	}

	public synchronized void Death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setCurrentHp(0);

			getMap().setPassable(getLocation(), true);

			// 删除魔法娃娃状态图示 
			if(_master instanceof L1PcInstance) {
               	L1PcInstance pc = (L1PcInstance)_master;
               	pc.sendPackets(new S_PacketBox(S_PacketBox.DOLL_ICON,0));
           	}
           	// 删除魔法娃娃状态图示  end
			deleteMe();
		}
	}

	// 消去处理
	@Override
	public synchronized void deleteMe() {
		if (_destroyed) {
			return;
		}
		if (!_tamed) {
			broadcastPacket(new S_SkillSound(getId(), 5936));
		}
		L1PcInstance masterpc = null;
        if (_master instanceof L1PcInstance) {
            masterpc = (L1PcInstance) _master;
            // 移除能力
            if (!_type.getPowerList().isEmpty()) {
                for (L1DollExecutor p : _type.getPowerList()) {
                    p.removeDoll(masterpc);
                }
            }
            final ArrayList<L1DollExecutor> dollExecutors = DollXiLianTable.get().getDollTypes(_itemObjId);
            if (dollExecutors != null && !dollExecutors.isEmpty()){
            	for (L1DollExecutor pxl : dollExecutors) {
                    pxl.removeDoll(masterpc);
                }
            }
            masterpc.sendPackets(new S_PacketBox(S_PacketBox.DOLL_ICON, 0));
        }
		_master.getPetList().remove(getId());
		super.deleteMe();

		if (_summonFuture != null) {
			_summonFuture.cancel(false);
			_summonFuture = null;
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_BabyPack(this, perceivedFrom));
	}

	public void set_currentPetStatus(int i) {
		_currentPetStatus = i;
		if (!isAiRunning()) {
			startAI();
		}
	}

	public int get_currentPetStatus() {
		return _currentPetStatus;
	}
	
	
	// 判断召唤后无法转移魔法娃娃道具 
	private int _itemObjId = 0;
	
	public int getItemObjId() {
		return _itemObjId;
	}
	
	public void setItemObjId(int i) {
		_itemObjId = i;
	}
	// 判断召唤后无法转移魔法娃娃道具  end
	
	public void setPowerList_Xl(ArrayList<L1DollExecutor> powerList_xl) {
        this.powerList_xl = powerList_xl;
    }
    
    public ArrayList<L1DollExecutor> getPowerList_Xl() {
        return powerList_xl;
    }
}