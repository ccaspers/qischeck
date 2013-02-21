package net.pixeltronics.qischeck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MIParser implements WebParser {
	/* (non-Javadoc)
	 * @see net.pixeltronics.qischeck.WebParser#readGrades(java.lang.String)
	 */
	@Override
	public List<Map<String,String>> readGrades(String html){
		html = html.replaceAll("\n|\r|\t|(&nbsp;)|(<!--\\s*-->)", "");
		List<Map<String,String>> parsedTable = new ArrayList<Map<String,String>>();
		
		Document doc = Jsoup.parse(html);
		
		Element table = doc.getElementsByTag("table").get(1);
		Elements rows = table.getElementsByTag("tr");
		rows.remove(0);
		rows.remove(0);
		for (Element row : rows){
			ArrayList<String> cells = new ArrayList<String>();
			int count = row.getElementsByAttributeValueNot("class", "qis_kontoOnTop").size();
			if(count > 2){
				for(Element cell : row.select("td")){
					cells.add(cell.text().trim());
				}
	
				if(!cells.isEmpty()){
					/*
					 * Prüfungsnr. 	   Prüfungstext 	Semester  	Termin 	Note 	Status 	Credit Points 	Vermerk 	Versuch 
					 * 
					 */
		    		String[] mapping = new String[] {"id", "title", "semester", "date", "result", "status", "creditpoints", "comment", "attempt" };
					Map<String,String> g = new HashMap<String,String>();
					for(int i = 0; i < mapping.length;i++){
						g.put(mapping[i],cells.get(i));
					}
					System.out.println(cells.toString());
					parsedTable.add(g);
				}
			}
		}		
		return parsedTable;
	}

	/* (non-Javadoc)
	 * @see net.pixeltronics.qischeck.WebParser#readCategories(java.lang.String)
	 */
	@Override
	public List<Map<String, String>> readCategories(String html) {
		html = html.replaceAll("\n|\r|\t|(&nbsp;)|(<!--\\s*-->)", "");
		List<Map<String,String>> parsedTable = new ArrayList<Map<String,String>>();
		
		Document doc = Jsoup.parse(html);
		
		Element table = doc.getElementsByTag("table").get(1);
		Elements rows = table.getElementsByTag("tr");
		rows.remove(0);
		rows.remove(0);
		for (Element row : rows){
			ArrayList<String> cells = new ArrayList<String>();
			int count = row.getElementsByAttributeValue("class", "qis_kontoOnTop").size();
			if(count > 2){
				for(Element cell : row.select("td")){
					if(cell.hasText()){
						cells.add(cell.text().trim());
					}
				}
	
				if(!cells.isEmpty()){
					/*
					 * Prüfungsnr. 	   Prüfungstext 	Semester  	Termin 	Note 	Status 	Credit Points 	Vermerk 	Versuch 
					 * 
					 */
		    		String[] mapping = new String[] {"id", "title", "result", "status", "creditpoints" };
					Map<String,String> g = new HashMap<String,String>();
					for(int i = 0; i < cells.size();i++){
						g.put(mapping[i],cells.get(i));
					}
					System.out.println(cells.toString());
					parsedTable.add(g);
				}
			}
		}		
		return parsedTable;
	}
}
