package com.example.shareholders.common;

import java.util.Comparator;

import com.example.shareholders.jacksonModel.personal.LocalFollowedStockFriend;

/**
 * 
 * @author xiaanming
 * 
 */
public class FollowedFriendPinyinComparator implements
		Comparator<LocalFollowedStockFriend> {

	public int compare(LocalFollowedStockFriend o1, LocalFollowedStockFriend o2) {
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
