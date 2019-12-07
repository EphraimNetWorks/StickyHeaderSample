package com.networks.testapplication;

import androidx.annotation.LayoutRes;

import com.saber.stickyheader.stickyData.HeaderData;

public class HeaderDataImpl implements HeaderData {
    public static final int HEADER = 10001;

    private int headerType;
    @LayoutRes
    private final int layoutResource;

    private String headerTitle;

    public HeaderDataImpl(int headerType, @LayoutRes int layoutResource, String headerTitle) {
        this.layoutResource = layoutResource;
        this.headerType = headerType;
        this.headerTitle = headerTitle;
    }

    @LayoutRes
    @Override
    public int getHeaderLayout() {
        //retunr layout of yourHeader
        return layoutResource;
    }

    @Override
    public int getHeaderType() {
        return headerType;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }
}
