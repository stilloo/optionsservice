package com.model.optionsservice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Expirations {

	private List<Expiration> expirations = new ArrayList<Expiration>();

	public List<Expiration> getExpirations() {
		return expirations;
	}

	public void setExpirations(List<Expiration> expirations) {
		this.expirations = expirations;
	}
	
	public void addExpiration(Expiration expiration) {
		this.expirations.add(expiration);
	}

	
	
}
