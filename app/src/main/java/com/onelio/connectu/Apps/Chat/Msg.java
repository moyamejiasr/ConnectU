package com.onelio.connectu.Apps.Chat;

/**
 * Created by Onelio on 04/03/2017.
 */

public class Msg {
    private String message;
    private boolean leMien;

    public Msg(boolean leMien, String message) {
        this.message = message;
        this.leMien = leMien;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getleMien() {
        return leMien;
    }

}
