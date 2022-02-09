package tech.sud.mgp.game.middle.manager;

import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerNotifyStateChange;

/**
 * app端调用sdk
 */
public class FsmApp2MgManager {

    private ISudFSTAPP iSudFSTAPP;

    /**
     * 设置app调用sdk的对象
     *
     * @param iSudFSTAPP
     */
    public void setISudFSTAPP(ISudFSTAPP iSudFSTAPP) {
        this.iSudFSTAPP = iSudFSTAPP;
    }

    /**
     * 更新code
     *
     * @param code
     * @param listener
     */
    public void updateCode(String code, ISudListenerNotifyStateChange listener) {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.updateCode(code, listener);
        }
    }

    public void onStart() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.startMG();
        }
    }

    public void onPause() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.pauseMG();
        }
    }

    public void onResume() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.playMG();
        }
    }

    public void onStop() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.stopMG();
        }
    }

    public void destroyMG() {
        if (iSudFSTAPP != null) {
            iSudFSTAPP.destroyMG();
        }
    }

}
