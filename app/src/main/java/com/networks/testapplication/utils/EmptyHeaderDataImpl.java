package com.networks.testapplication.utils;

import androidx.annotation.LayoutRes;

import com.networks.testapplication.R;
import com.saber.stickyheader.stickyData.HeaderData;

public class EmptyHeaderDataImpl implements HeaderData {
    public static final int EMPTY_HEADER = 10004;

    private int headerType;
    @LayoutRes
    private final int layoutResource;


    public EmptyHeaderDataImpl() {
        this.layoutResource = R.layout.item_empty_sticky_header;
        this.headerType = EMPTY_HEADER;
    }

    @LayoutRes
    @Override
    public int getHeaderLayout() {
        //return layout of yourHeader
        return layoutResource;
    }

    @Override
    public int getHeaderType() {
        return headerType;
    }
}
