package net.pixeltronics.qischeck.qis;

import java.util.ArrayList;
import java.util.List;

import net.pixeltronics.qischeck.db.GradesContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;

public class MIParser implements WebParser {
	/* (non-Javadoc)
	 * @see net.pixeltronics.qischeck.WebParser#readGrades(java.lang.String)
	 */
	@Override
	public List<ContentValues> readGrades(String html){
		html = html.replaceAll("\n|\r|\t|(&nbsp;)|(<!--\\s*-->)", "");
		List<ContentValues> parsedTable = new ArrayList<ContentValues>();
		
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
		    		String[] mapping = GradesContract.Grade.PROJECTION_FULL;
					ContentValues g = new ContentValues();
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
	public List<ContentValues> readCategories(String html) {
		html = html.replaceAll("\n|\r|\t|(&nbsp;)|(<!--\\s*-->)", "");
		List<ContentValues> parsedTable = new ArrayList<ContentValues>();
		
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
		    		String[] mapping = GradesContract.Category.PROJECTION_FULL;
					ContentValues g = new ContentValues();
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
