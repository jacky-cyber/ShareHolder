package com.example.shareholders.util;

public class ModeModel {

	int moreStatu;// 0-未度量 1-行数小于4 2--行数大于4

	String context;

	boolean isMore;

	public int getMoreStatu() {
		return moreStatu;
	}

	public void setMoreStatu(int moreStatu) {
		this.moreStatu = moreStatu;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean isMore() {
		return isMore;
	}

	public void setMore(boolean isMore) {
		this.isMore = isMore;
	}

}
