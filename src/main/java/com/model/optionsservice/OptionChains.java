package com.model.optionsservice;

import java.util.ArrayList;
import java.util.List;

public class OptionChains {

	private List<OptionChain> optionChains = new ArrayList<OptionChain>();

	public List<OptionChain> getOptionsChain() {
		return optionChains;
	}

	public void setOptionsChain(List<OptionChain> optionChains) {
		this.optionChains = optionChains;
	}
	
	public void addOptionChain(OptionChain optionChain) {
		this.optionChains.add(optionChain);
	}
}
