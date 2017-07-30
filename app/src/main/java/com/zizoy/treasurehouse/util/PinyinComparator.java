package com.zizoy.treasurehouse.util;

import com.zizoy.treasurehouse.model.CityModel;

import java.util.Comparator;

/**
 * @Description: 拼音分类
 * 
 * @author 刘海标
 */
public class PinyinComparator implements Comparator<CityModel> {
	public int compare(CityModel c1, CityModel c2) {
		if (c1.getSortLetters().equals("☆") || c2.getSortLetters().equals("#")) {
			return -1;
		} else if (c1.getSortLetters().equals("#") || c2.getSortLetters().equals("☆")) {
			return 1;
		} else {
			return c1.getSortLetters().compareTo(c2.getSortLetters());
		}
	}
}