package com.resources.optionsservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.model.optionsservice.Expiration;
import com.model.optionsservice.Expirations;
import com.model.optionsservice.OptionChain;
import com.model.optionsservice.OptionChains;
import com.model.optionsservice.Quotes;


/**
 * Root resource 
 */
@Path("optionsresource")
public class OptionsResource {

	/**
	 * Get price of stock given its stock ticker
	 * @param ticker
	 * @return Quotes
	 */
    @GET
    @Path("quotes/{ticker}")
    @Produces(MediaType.APPLICATION_JSON)
    public Quotes getPrice(@PathParam("ticker") String ticker) {
    	String url = "http://download.finance.yahoo.com/d/quotes.csv?s="+ticker+"&f=l1&e=.csv";
    			//"http://www.google.com/finance/option_chain?q=ebay&output=json";
    	StringBuilder str = new StringBuilder();
    	Quotes quotes = new Quotes();
		
    	 try {
    		 	HttpClient httpClient = HttpClientBuilder.create().build();
    			HttpGet getRequest = new HttpGet(url);
    			HttpResponse response = httpClient.execute(getRequest);
    			if (response.getStatusLine().getStatusCode() != 200) {
    				throw new RuntimeException("Failed : HTTP error code : "
    				   + response.getStatusLine().getStatusCode());
    			}
    			BufferedReader br = new BufferedReader(
    	                         new InputStreamReader((response.getEntity().getContent())));
    			String output = null;
    			while ((output = br.readLine()) != null) {
    				str.append(output);
    			}
    			quotes.setPrice(str.toString());
		 } catch (ClientProtocolException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
    	 return quotes;
    }
    
    /**
     * Get expiration dates for all options given its stock ticker
     * @param ticker
     * @return Expirations
     * @throws IOException
     */
    @GET
    @Path("expirations/{ticker}")
    @Produces(MediaType.APPLICATION_JSON)
    public Expirations getExpiration(@PathParam("ticker") String ticker) throws IOException {
    	
    	Document doc = Jsoup.connect("http://finance.yahoo.com/q/op?s="+ticker).get();
    	Elements expirationElements = doc.select("option[data-selectbox-link^=/q/op?s="+ticker.toUpperCase()+"]");
    	Expirations expirations = new Expirations();
    	
    	for(int i = 0 ;i < expirationElements.size();i++) {
    		System.out.println(expirationElements.get(i));
    		Element element = expirationElements.get(i);
    		
    		Expiration expiration = new Expiration();
    		expiration.setDisplayText(element.text());
    		expiration.setExpiration(element.attr("value"));
    		expirations.addExpiration(expiration);
    	}
    	
    	return expirations;
    }
    
    /**
     * Get options chain (strike and premium) given a ticker, expiration and type (call/put)
     * @param ticker
     * @param expiration
     * @param type
     * @return OptionChains
     * @throws IOException
     */
    @GET
    @Path("optionschain/{ticker}/{expiration}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OptionChains> getOptionsChain(@PathParam("ticker") String ticker, @PathParam("expiration") String expiration, @QueryParam("type") String type) throws IOException {
    	Document doc = Jsoup.connect("http://finance.yahoo.com/q/op?s="+ticker+"&date="+expiration).get();
    	List<OptionChains> optionChainsList = new ArrayList<OptionChains>();
    	if(!"C".equalsIgnoreCase(type) && !"P".equalsIgnoreCase(type)){
    		//type is not C or P
    		optionChainsList.add(populateOptionChains(ticker, "C", doc, expiration));
    		optionChainsList.add(populateOptionChains(ticker, "P", doc, expiration));
    	} else {
    		optionChainsList.add(populateOptionChains(ticker, type, doc, expiration));
    	}
    	
    	
    	return optionChainsList;
    }

	private OptionChains populateOptionChains(String ticker, String type, Document doc, String expiration) throws IOException {
		OptionChains optionChains = new OptionChains();
		Elements optionsTable = null;
		if("C".equalsIgnoreCase(type) ) {
    		optionsTable = doc.select("div[id=optionsCallsTable]");
    	} else if("P".equalsIgnoreCase(type)){
    		optionsTable = doc.select("div[id=optionsPutsTable]");
    	} 
		Elements strikeElements= optionsTable.select("a[href^=/q/op?s="+ticker.toUpperCase()+"&strike]");
		optionChains.setType(type);
    	for(int i = 0 ;i < strikeElements.size();i++) {
    		System.out.println(strikeElements.get(i));
    		Element element = strikeElements.get(i);
    		Element lastPremiumElement = element.parent().parent().nextElementSibling().nextElementSibling().child(0);
    		OptionChain optionChain = new OptionChain();
    		optionChain.setStrike(element.text());
    		optionChain.setPremium(lastPremiumElement.text());
    		optionChains.addOptionChain(optionChain);
    	}
    	
		return optionChains;
	}
    
 
   
}
