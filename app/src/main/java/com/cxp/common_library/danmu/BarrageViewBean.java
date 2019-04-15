package com.cxp.common_library.danmu;

import java.io.Serializable;

/**
 * 弹幕bean
 */
public class BarrageViewBean implements Serializable {
    private String content;

    public BarrageViewBean(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getContent() {
        return content;
    }


}
