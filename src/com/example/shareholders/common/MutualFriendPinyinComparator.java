package com.example.shareholders.common;

import java.util.Comparator;

import com.example.shareholders.jacksonModel.personal.LocalMutualStockFriend;

/**
 * 
 * @author xiaanming
 * 
 */
public class MutualFriendPinyinComparator implements
		Comparator<LocalMutualStockFriend> {

	public int compare(LocalMutualStockFriend o1, LocalMutualStockFriend o2) {
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
