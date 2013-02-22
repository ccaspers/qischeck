package net.pixeltronics.qischeck.db;

import net.pixeltronics.qischeck.db.DBContract.Table;
import android.net.Uri;

public class GradesContract {
	private GradesContract(){}
	
	public static final String AUTH = "net.pixeltronics.qischeck.db";
	public static final Uri BASE_URI = Uri.parse("content://" + AUTH);
	
	public static final String GRADES = "Grades";
	public static final String CATEGORIES = "Categories"; 
	
	public static class Grade{
		private Grade(){};
		public static final Uri BASE_URI = Uri.withAppendedPath(GradesContract.BASE_URI, GRADES);
		public static final String[] PROJECTION_FULL = new String[] {
				Table.Grade.ID, 
				Table.Grade.TITLE, 
				Table.Grade.SEMESTER,
				Table.Grade.STATUS, 
				Table.Grade.RESULT, 
				Table.Grade.DATE,
				Table.Grade.CP, 
				Table.Grade.COMMENT
		};
		public static final String[] PROJECTION_SMALL = new String[] {
			Table.Grade.ID, 
			Table.Grade.TITLE, 
			Table.Grade.SEMESTER,
			Table.Grade.RESULT, 
		};
		public static final String SELECTION_BY_CATEGORY = " id/10 = ?/10"; 
	}
	
	public static class Category{
		private Category(){};
		public static final Uri BASE_URI = Uri.withAppendedPath(GradesContract.BASE_URI, CATEGORIES);
		public static final String[] PROJECTION_FULL = new String[]{
				Table.Category.ID,
				Table.Category.TITLE,
				Table.Category.RESULT,
				Table.Category.STATUS,
				Table.Category.CP
		};
	}
}	

