package com.networks.testapplication.ui.adapters_viewholders;

import androidx.annotation.LayoutRes;

import com.saber.stickyheader.stickyData.HeaderData;

import org.threeten.bp.ZonedDateTime;

public class DateHeaderDataImpl implements HeaderData {
    public static final int HEADER = 10001;

    private int headerType;
    @LayoutRes
    private final int layoutResource;

    private ZonedDateTime headerDate;

    public DateHeaderDataImpl(int headerType, @LayoutRes int layoutResource, ZonedDateTime headerTitle) {
        this.layoutResource = layoutResource;
        this.headerType = headerType;
        this.headerDate = headerTitle;
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

    public ZonedDateTime getHeaderDate() {
        return headerDate;
    }
}
