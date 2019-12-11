package com.networks.testapplication.utils;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.networks.testapplication.R;

public class NetworkState {

    private Status status;

    @StringRes private Integer messageResId;

    @StringRes private Integer messageBodyResId;

    @DrawableRes
    private Integer iconResId ;

    private boolean isResponseEmpty = true;

    private NetworkState(Status status, Integer iconResId, Integer messageResId, Integer messageBodyResId, boolean isResponseEmpty) {
        this.status = status;
        this.messageResId = messageResId;
        this.iconResId = iconResId;
        this.messageBodyResId = messageBodyResId;
        this.isResponseEmpty = isResponseEmpty;
    }

    public static NetworkState loading(){ return new NetworkState(Status.RUNNING,null,null,null,false); }

    public static NetworkState loading (boolean isResponseEmpty){
        return new NetworkState(Status.RUNNING,null,null,null,isResponseEmpty);
    }

    public static NetworkState error(boolean isResponseEmpty, Integer messageResId, Integer messageBodyResId) {
        return new NetworkState(Status.FAILED,
                R.drawable.ic_no_internet,
                messageResId == null ? R.string.unknown_error : messageResId,
                messageBodyResId == null ? R.string.unknown_error : messageBodyResId,
                isResponseEmpty);
    }

    public static NetworkState loaded(boolean isResponseEmpty,
                                      Integer iconResId,
                                      Integer messageResId,
                                      Integer messageBodyResId) {
        return new NetworkState(Status.SUCCESS,iconResId, messageResId, messageBodyResId, isResponseEmpty);
    }

    public static NetworkState loaded() {
        return new NetworkState(Status.SUCCESS,null,null,null,false);
    }

    public Status getStatus() {
        return status;
    }

    public Integer getMessageResId() {
        return messageResId;
    }

    public Integer getMessageBodyResId() {
        return messageBodyResId;
    }

    public boolean isResponseEmpty() {
        return isResponseEmpty;
    }

    public Integer getIconResId() {
        return iconResId;
    }
}

