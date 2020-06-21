package com.nttn.coolandroid.custom.calender;

public class DateBean {

    /**
     * 日期
     */
    private String date;
    /**
     * 底部文字
     */
    private String bottomString;
    /**
     * 中间文字
     */
    private String centerString;

    /**
     * 是否可以选择
     */
    private boolean canSelect;
    /**
     * 是否选中
     */
    private boolean isSelect;

    /**是否在选中范围*/
    private boolean isSelectRange;
    /**
     * item check_type 0月份 1日期
     */
    private int type;


    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCenterString() {
        return centerString == null ? "" : centerString;
    }

    public void setCenterString(String centerString) {
        this.centerString = centerString;
    }

    public boolean isCanSelect() {
        return canSelect;
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isSelectRange() {
        return isSelectRange;
    }

    public void setSelectRange(boolean selectRange) {
        isSelectRange = selectRange;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBottomString() {
        return bottomString == null ? "" : bottomString;
    }

    public void setBottomString(String bottomString) {
        this.bottomString = bottomString;
    }
}
