package com.example.shareholders.common;

import java.util.Comparator;

import com.example.shareholders.jacksonModel.personal.LocalFollowStockFriend;

/**
 * 
 * @author xiaanming
 * 
 */
public class FollowFriendPinyinComparator implements
		Comparator<LocalFollowStockFriend> {

	public int compare(LocalFollowStockFriend o1, LocalFollowStockFriend o2) {
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
