package com.model.optionsservice;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Quotes{

	private String price;

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	
}
