package pl.pisz.airlog.giepp.data.gpw;

import pl.pisz.airlog.giepp.data.ArchivedStock;
import pl.pisz.airlog.giepp.data.CurrentStock;
import pl.pisz.airlog.giepp.data.DataParser;
import pl.pisz.airlog.giepp.data.BadDate;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class GPWDataParser implements DataParser {
	
	private Pattern patternArchive;
	private Pattern patternCurrent;

	public GPWDataParser() {
		patternArchive = Pattern.compile(".*<td .*>(.*)</td>.*<td .*>.*</td>.*<td>.*</td>.*<td>.*</td>.*<td>(.*)</td>.*<td>(.*)</td>.*<td>(.*)</td>.*<td>.*</td>.*<td>.*</td>.*<td>.*</td>.*<td>.*</td>");
		patternCurrent = Pattern.compile(".*<td>.*</td><td class=\"left nowrap\"><a.*>(.*)</a></td><td class=\"left\">.*</td><td class=\"left\">PLN</td><td>(.*)</td><td>.*</td><td>.*</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>.*</td><td>.*</td>");
	}

	/** Zwraca listę notowań archiwalnych z podanego dnia. Jeśli dla danego dnia nie ma wyników to wyrzuca wyjątek BadDate **/
	public ArrayList<ArchivedStock> parseArchive(String site) throws BadDate {
		String table = findTable(site);
		String[] lines = splitToLines(table);
		
		ArrayList<ArchivedStock> all = new ArrayList<ArchivedStock>();
		
		for (int i = 0 ; i < lines.length ; i++) {
			ArchivedStock single = matchSingleStockArchive(lines[i]);
			if (single != null) {
				all.add(single);
			}
		}
				
		if (all.size()==0) {
			throw new BadDate();
		}
		else {
			return all;
		}
	}
	
	/** Zwraca aktualną listę notowań **/
	public ArrayList<CurrentStock> parseCurrent(String site) {
		String table = findTable(site);
		String[] lines = splitToLines(table);	
 		ArrayList<CurrentStock> all = new ArrayList<CurrentStock>();

		for (int i = 0 ; i < lines.length ; i++) {
			CurrentStock single = matchSingleStockCurrent(lines[i]);
			if (single != null) {
				all.add(single);
			}
		}
		
		return all;
	}
	
	private String findTable(String site) {
		Pattern p = Pattern.compile("<table class=\"tab03\">(.*)</table>");
		Matcher m = p.matcher(site);
		String table = "";
		if ( m.find() ){
			table = m.group(1);
		}
		return table;
	}
	
	private String[] splitToLines(String table) {
		return table.split("</tr>");
	}
	
	private ArchivedStock matchSingleStockArchive(String line) {
		
		Matcher m = patternArchive.matcher(line);
		float max = 0.0f;
		float min = 0.0f;
		float end = 0.0f;
			 
		if (m.find()) {
			try{
				max = Float.parseFloat(m.group(2).replaceAll(",","."));
			}catch (Exception e) {}
			try{
				min = Float.parseFloat(m.group(3).replaceAll(",","."));
			}catch (Exception e) {}
			try{
				end = Float.parseFloat(m.group(4).replaceAll(",","."));
			}catch (Exception e) {}
			
			if (max == 0.0f) {
				max = end;
				min = end;
			}

//			System.out.print(m.group(1) + "\t"+ max +  "\t"+ min + "\n");
			return new ArchivedStock(m.group(1),max,min);
		}
		return null;
	}

	private CurrentStock matchSingleStockCurrent(String line) {
		Matcher m = patternCurrent.matcher(line);
		float start = 0.0f;
		float max = 0.0f;
		float min = 0.0f;
		float end = 0.0f;
		float change = 0.0f;

		if (m.find()) {
			try{
				start = Float.parseFloat(m.group(3).replaceAll(",","."));
			}catch (Exception e) {}
			try{
				min = Float.parseFloat(m.group(4).replaceAll(",","."));
			}catch (Exception e) {}
			try{
				max = Float.parseFloat(m.group(5).replaceAll(",","."));
			}catch (Exception e) {}	
			try{
				end = Float.parseFloat(m.group(6).replaceAll(",","."));
			}catch (Exception e) {}
			try{
				change = Float.parseFloat(m.group(7).replaceAll(",","."));
			}catch (Exception e) {}
//			System.out.print(m.group(1) + " " + m.group(2) +" " + start +" " + min +" " + max +" " + end + " " + change +"\n");
			return new CurrentStock(m.group(1),m.group(2),start,min,max,end,change);
		}		
		return null;
	}	
}