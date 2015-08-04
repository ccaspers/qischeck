package net.pixeltronics.qischeck.db;

import android.provider.BaseColumns;

public final class DBContract {
	private DBContract(){};
	
	public static final String DBName = "grades.db";
	public static final int VERSION = 1;
	
	public static final class Table{
		private Table(){};
		
		public static final String CATEGORY = "category";
		public static final String GRADE = "grade";
		
		private static final String CREATE_TABLE = "CREATE TABLE ";
		
		public static final class Category implements BaseColumns{
			private Category(){};
			
			public static final String TITLE 	= "title";
			public static final String RESULT 	= "result";
			public static final String STATUS 	= "status";
			public static final String CP 		= "creditpoints";
			
			public static final String CREATE = CREATE_TABLE + CATEGORY + " ("
					+ _ID		+ " INTEGER PRIMARY KEY" 	+ ", "
					+ TITLE		+ " TEXT" 					+ ", "
					+ RESULT	+ " TEXT" 					+ ", "
					+ STATUS    + " TEXT" 					+ ", "
					+ CP		+ " TEXT"
					+")";
		}
		
		public static final class Grade implements BaseColumns{
			private Grade(){};
			
			public static final String TITLE 	= "title";
			public static final String SEMESTER = "semester";
			public static final String DATE 	= "date";
			public static final String RESULT 	= "result";
			public static final String STATUS 	= "status";
			public static final String CP 		= "creditpoints";
			public static final String COMMENT 	= "comment";
			public static final String ATTEMPT 	= "attempt";
			
			public static final String CREATE = CREATE_TABLE + GRADE + "("
					+ _ID		+ " INTEGER PRIMARY KEY"	+ ", "
					+ TITLE		+ " TEXT"					+ ", "
					+ SEMESTER	+ " TEXT"					+ ", "
					+ DATE		+ " TEXT"					+ ", "
					+ RESULT	+ " TEXT"					+ ", "
					+ STATUS	+ " TEXT"					+ ", "
					+ CP		+ " TEXT"					+ ", "
					+ COMMENT	+ " TEXT"					+ ", "
					+ ATTEMPT	+ " TEXT"
					+ ")";
			
		}
	}
}
