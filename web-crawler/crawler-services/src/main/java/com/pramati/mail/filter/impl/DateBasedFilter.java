package com.pramati.mail.filter.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pramati.mail.filter.IMailFilter;

public class DateBasedFilter implements IMailFilter {

	public Boolean evaluate(String regex, String content) {
		
		String dateRegex="Date:(.*)"+regex;
		Pattern pattern = Pattern.compile(dateRegex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return true;
		}
		return false;

	}

}