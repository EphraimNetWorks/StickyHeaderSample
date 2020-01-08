package com.networks.testapplication.ui.adapters_viewholders;

import androidx.annotation.LayoutRes;

import com.saber.stickyheader.stickyData.HeaderData;

import org.threeten.bp.LocalDate;

public class DateHeaderDataImpl implements HeaderData {
    public static final int HEADER = 10001;

    private int headerType;
    @LayoutRes
    private final int layoutResource;

    private LocalDate headerDate;

    public DateHeaderDataImpl(int headerType, @LayoutRes int layoutResource, LocalDate headerTitle) {
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

    public LocalDate getHeaderDate() {
        return headerDate;
    }
}
