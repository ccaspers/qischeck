package net.pixeltronics.qischeck.qis;

import java.util.ArrayList;
import java.util.List;

import net.pixeltronics.qischeck.db.DBContract.Table.Category;
import net.pixeltronics.qischeck.db.DBContract.Table.Grade;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ContentValues;

public class MIParser implements WebParser {
	
	private static final String[] gradeMapping =  
			new String[]{Grade._ID, Grade.TITLE, Grade.SEMESTER, Grade.DATE, Grade.RESULT, Grade.STATUS, Grade.CP, Grade.COMMENT, Grade.ATTEMPT};
	
	private static final String[] categoryMapping =
			new String[] {Category._ID, Category.TITLE, Category.RESULT, Category.STATUS, Category.CP};

	/* (non-Javadoc)
	 * @see net.pixeltronics.qischeck.WebParser#readGrades(java.lang.String)
	 */
	@Override
	public List<ContentValues> readGrades(String html){
		List<ContentValues> parsedTable = new ArrayList<ContentValues>();
		Elements rows = findTable(html);

		for (Element row : rows){
			int count = row.getElementsByAttributeValueNot("class", "qis_kontoOnTop").size();
			if(count > 2){
				ArrayList<String> cells = extractCellsFromRow(row);
				if(!cells.isEmpty()){
					ContentValues g = mapCellsToKeys(cells,gradeMapping);
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
		List<ContentValues> parsedTable = new ArrayList<ContentValues>();
		Elements rows = findTable(html);
		for (Element row : rows){
			
			int count = row.getElementsByAttributeValue("class", "qis_kontoOnTop").size();
			if(count > 2){
				ArrayList<String> cells = extractCellsFromRow(row);
				if(!cells.isEmpty()){
					ContentValues g = mapCellsToKeys(cells,categoryMapping);
					parsedTable.add(g);
				}
			}
		}		
		return parsedTable;
	}

	private ArrayList<String> extractCellsFromRow(Element row) {
		ArrayList<String> cells = new ArrayList<String>();
		for(Element cell : row.select("td")){
			if(cell.hasText()){
				cells.add(cell.text().trim());
			}
		}
		return cells;
	}

	private ContentValues mapCellsToKeys(ArrayList<String> cells, String[] mapping) {
		ContentValues g = new ContentValues();
		for(int i = 0; i < cells.size();i++){
			g.put(mapping[i],cells.get(i));
		}
		return g;
	}

	private Elements findTable(String html) {
		html = html.replaceAll("\n|\r|\t|(&nbsp;)|(<!--\\s*-->)", "");
		Document doc = Jsoup.parse(html);
		Element table = doc.getElementsByTag("table").get(1);
		Elements rows = table.getElementsByTag("tr");
		rows.remove(0);
		rows.remove(0);
		return rows;
	}
}
