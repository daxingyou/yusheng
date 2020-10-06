package l1j.server.server.model.guaji;

import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1PcAI implements Runnable {
	private static final Log _log = LogFactory.getLog(L1PcAI.class);
	private static Random _random = new Random();
	private final L1PcInstance _pc;
    public L1PcAI(final L1PcInstance pc) {
        _pc = pc;
    }
    
    public void startAI() {
    	GeneralThreadPool.getInstance().execute(this);
    }
    
    @Override
    public void run() {
        try {
        	// 关闭自动状态
        	_pc.setskillAuto(false);
        	int loop = 10;
        	for (; loop > 0;loop--){
        		if (_pc.isActived()){
    				_pc.sendPackets(new S_SystemMessage(String.format("\\aD 自动挂机%d秒后正式开启。", loop)));
    			}else{
    				_pc.sendPackets(new S_SystemMessage(String.format("\\aD 自动挂机%d秒后停止。", loop)));
    			}
        		_pc.setPcAILoop(loop);
        		Thread.sleep(1000);
        	}
        	_pc.setStartGuaJiLoc(new L1Location(_pc.getLocation()));
            while (_pc.getMaxHp() > 0) {
                while (_pc.isSleeped() || _pc.isParalyzed()) {
                    Thread.sleep(200);
                }
                if (AIProcess()) {
                	_pc.setAiRunning(false);
                	_pc.allTargetClear();
                    break;
                }
                try {
                    Thread.sleep(_pc.getSleepTime());
                } catch (final Exception e) {
                    break;
                }
            }
            do {
                try {
                    Thread.sleep(100);
                } catch (final Exception e) {
                    break;
                }
            } while (_pc.isDead());

            _pc.allTargetClear();
            _pc.setAiRunning(false);
            _pc.setActived(false);
            Thread.sleep(10);

        } catch (final Exception e) {
            _log.error("pcAI發生例外狀況: " + this._pc.getName(), e);
        }
    }
    /**
     * AI的處理
     * 
     * @return true:AI終了 false:AI續行
     */
    private boolean AIProcess() {
        try {
//        	if (_pc.getMapId() != 53 && _pc.getMapId() != 54 && _pc.getMapId() != 55
//    				&& _pc.getMapId() != 56) {
//    			_pc.sendPackets(new S_SystemMessage("\\aD非挂机地图无法开启！"));
//    			_pc.setActived(false);
//    			_pc.removeSkillEffect(L1SkillId.STATUS_BRAVE);
//    			//_pc.setskillAuto_gj(false);
//    			return true;
//    		}
        	
        	if (_pc.getInventory().getWeight240() >= 197) { // 重量
				_pc.sendPackets(new S_SystemMessage("因负重超过82，无法战斗，系统停止挂机.并且回城")); // \f1重
				final L1Location newLocation = new L1Location(
						33437, 32812, 4).randomLocation(10, false);
				L1Teleport.teleport(_pc, newLocation.getX(),
						newLocation.getY(),
						(short) newLocation.getMapId(), 5, true);
				return true;
			}
        	
        	if (_pc.getInventory().getSize() > 180 - 16) {
        		_pc.sendPackets(new S_SystemMessage("背包超过最大限制，无法战斗，系统停止挂机.并且回城")); // \f1重。
        		final L1Location newLocation = new L1Location(
						33437, 32812, 4).randomLocation(10, false);
				L1Teleport.teleport(_pc, newLocation.getX(),
						newLocation.getY(),
						(short) newLocation.getMapId(), 5, true);
				return true;
        	}
        	
            if (_pc.isDead()) {
                return true;
            }
            
            if (_pc.getOnlineStatus() == 0) {
                return true;
            }

            if (_pc.getCurrentHp() <= 0) {
                return true;
            }
            if (!_pc.isActived()) {
            	L1Teleport.teleport(_pc, _pc.getLocation(), _pc.getHeading(), false);
            	return true;
            }
            _pc.checkTarget();

            boolean searchTarget = true;
            if (_pc.is_now_target() != null) {
                searchTarget = false;
            }
            
            
            if (searchTarget) {
                _pc.searchTarget();
            }
            
            if (_pc.is_now_target() == null) {
            	if (!_pc.isPathfinding()) {
            		_pc.setrandomMoveDirection(_random.nextInt(8));
            	}
                _pc.noTarget();
            	Thread.sleep(50);
                return false;
            } else {
            	_pc.onTarget();
            	if (_pc.isPathfinding()) {
            		_pc.setPathfinding(false);
            	}
            }

            Thread.sleep(50);

        } catch (final Exception e) {
            _log.error("pcAI發生例外狀況: " + this._pc.getName(), e);
        }
        return false; // NPC AI 繼續執行
    }
}
