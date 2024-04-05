package com.cricket.containers;

import java.util.Arrays;

public class Excel {

	private String grafix;
    private String size;
    private String header;
    private String subHeader;
    private String trioPage;
    private String[][] table;

    public Excel() {
    }
    
    public String getGrafix() {
		return grafix;
	}

	public void setGrafix(String grafix) {
		this.grafix = grafix;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getSubHeader() {
		return subHeader;
	}

	public void setSubHeader(String subHeader) {
		this.subHeader = subHeader;
	}

	public String[][] getTable() {
		return table;
	}

	public void setTable(String[][] table) {
		this.table = table;
	}

	public String getTrioPage() {
		return trioPage;
	}

	public void setTrioPage(String trioPage) {
		this.trioPage = trioPage;
	}

	@Override
	public String toString() {
		return "Excel [grafix=" + grafix + ", size=" + size + ", header=" + header + ", subHeader=" + subHeader
				+ ", trioPage=" + trioPage + ", table=" + Arrays.toString(table) + "]";
	}
}
	