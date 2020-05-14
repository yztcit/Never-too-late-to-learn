package com.chen.coolandroid.tool.networkstate;

/**
 * Created by Apple.
 * Desc: 网络状态类型
 */
public enum NetworkState {
    AUTO             ( "AUTO"  ),//全部状态
    WIFI             ( "WIFI"  ),
    SECOND_GENERATION( "2G"    ),
    THIRD_GENERATION ( "3G"    ),
    FOUR_GENERATION  ( "4G"    ),
    FIVE_GENERATION  ( "5G"    ),
    MOBILE           ( "MOBILE"),
    NONE             ( "NONE"  );//无网络

    private String stateName;

    NetworkState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
}
