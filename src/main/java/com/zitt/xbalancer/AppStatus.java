package com.zitt.xbalancer;

/**
 * Created by lkrzywda on 2/3/18.
 */
public class AppStatus {

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";

    private String status;

    /**
     * @param status
     */
    public AppStatus(String status) {
        this.status = status;
    }

    /**
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
