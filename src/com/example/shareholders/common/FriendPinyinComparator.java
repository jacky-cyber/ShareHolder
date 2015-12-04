package com.example.shareholders.common;

import java.util.Comparator;

import com.example.shareholders.jacksonModel.personal.StockFriend;

/**
 * 
 * @author xiaanming
 * 
 */
public class FriendPinyinComparator implements Comparator<StockFriend> {

	public int compare(StockFriend o1, StockFriend o2) {
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
