package com.chen.coolandroid.tool.network;

/**
 * Created by Apple.
 * Desc: 网络状态类型
 */
public enum NetworkState {
    AUTO             (1, "AUTO"),//全部状态
    WIFI             (2, "WIFI"),
    SECOND_GENERATION(3, "2G"),
    THIRD_GENERATION (4, "3G"),
    FOUR_GENERATION  (5, "4G"),
    FIVE_GENERATION  (6, "5G"),
    MOBILE           (7, "MOBILE"),
    NONE             (8, "NONE");//无网络
    private int state;
    private String stateName;

    NetworkState(int state, String stateName) {
        this.state = state;
        this.stateName = stateName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
