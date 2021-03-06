package com.example.ehentaiapp;

import java.util.HashMap;
import java.util.Map;

public class Filter {
	public static final String[] ARRAY_OF_SEARCH_OPTION_FILTER = {
		"doujinshi", "manga", "artistcg", "gamecg", "western",
		"non-h", "imageset", "cosplay", "asianporn", "misc"};
	
	public static final String[] KEY_OF_FILTER = {
		"f_doujinshi", "f_manga", "f_artistcg", "f_gamecg", "f_western",
		"f_non-h", "f_imageset", "f_cosplay", "f_asianporn", "f_misc"};

	public static final Map<String, Integer> MAP_FILTER;
	static {
		MAP_FILTER = new HashMap<String, Integer>();
		for(int i=0; i<ARRAY_OF_SEARCH_OPTION_FILTER.length; i++) {
			MAP_FILTER.put(ARRAY_OF_SEARCH_OPTION_FILTER[i], i);
		}
	}
	
	public static final String KEY_OF_SEARCH = "f_search";
	public static final String KEY_OF_APPLY = "f_apply";
	
	private boolean[] searchOption;
	
	public Filter() {
		// TODO Auto-generated constructor stub
		searchOption = new boolean[] {
			true, true, false, false, false, 
			false, false, false, false, false
		};
	}
	
	public static String getOption(boolean opt) {
		return opt ? "1" : "0";
	}

	public static String getOptionText(int index) {
		return ARRAY_OF_SEARCH_OPTION_FILTER[index];
	}
	
	public String getOption(int index) {
		return searchOption[index] ? "1" : "0";
	}
	
	public boolean[] getOption() {
		return searchOption;
	}
}
