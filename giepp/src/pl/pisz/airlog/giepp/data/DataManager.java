package pl.pisz.airlog.giepp.data;

import java.util.ArrayList;
import java.util.TreeMap;
import java.io.*;

public class DataManager {
	
	private DataParser parser;
	private DataSource source;
	private LocalStorage storage;

	public DataManager(DataSource source, DataParser parser, LocalStorage storage){
		this.source = source;
		this.parser = parser;
		this.storage = storage;
	}
	
	public ArrayList<ArchivedStock> getArchival(int day, int month, int year) throws BadDate, IOException {
		ArrayList<ArchivedStock> archived = parser.parseArchive(source.retrieveArchiveData(day,month,year));
        String dayS = day+"";
        if (day < 10) {
            dayS = "0"+day;
        }
        String monthS = month+"";
        if (month < 10) {
            monthS = "0"+month;
        }
		String date = year+"-"+monthS+"-"+dayS;

		for (int i = 0; i < archived.size(); i++)
			archived.get(i).setDate(date);
		return archived;
	}
	
	public ArrayList<CurrentStock> getCurrent() throws IOException {
		return parser.parseCurrent(source.retrieveCurrentData());
	}

	public ArrayList<PlayerStock> getOwned() {		
		return this.storage.getOwned();
	}
	
	public ArrayList<String> getObserved() {		
	    ArrayList<String> observed = null;
        try {
            observed = storage.getObserved();
        } catch (IllegalArgumentException e) {
            observed = new ArrayList<String>(); 
        }
        
        return observed;
	}
	
	public TreeMap<String,ArrayList<ArchivedStock>> getArchivalFromXML() {		
		return this.storage.getArchivalFromXML();
	}
	
	public Stats getStats() {
		Stats stat = null;
		try {
			stat = storage.getStats();
		} catch (IllegalArgumentException e) {
			stat = new Stats(); 
		}
		
		return stat;
	}

	public void saveStats(Stats stats) {
		try {
			storage.saveStats(stats);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void saveObserved(ArrayList<String> observed)
	        throws IOException {
	    storage.saveObserved(observed);
	}

	public void saveArchival(TreeMap<String,ArrayList<ArchivedStock>> archived) throws IOException {
	    this.storage.saveArchival(archived);
	}
	
	public void saveOwned(ArrayList<PlayerStock> owned) throws IOException {
	    this.storage.saveOwned(owned);
	}

}

