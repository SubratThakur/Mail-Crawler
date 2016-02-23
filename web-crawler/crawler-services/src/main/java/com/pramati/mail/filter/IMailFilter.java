package com.pramati.mail.filter;

public interface IMailFilter {

	public Boolean evaluate(String regex, String content);
}
