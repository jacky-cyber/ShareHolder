package com.example.shareholders.common;

public class LocalContactModel {

	private String name;
	private String sortLetters;
	private String number;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	@Override
	public String toString() {
		return "LocalContactModel [name=" + name + ", sortLetters="
				+ sortLetters + ", number=" + number + "]";
	}

}
