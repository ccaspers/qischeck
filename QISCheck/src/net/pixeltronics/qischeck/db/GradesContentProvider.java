package net.pixeltronics.qischeck.db;

import net.pixeltronics.qischeck.db.GradesContract.Category;
import net.pixeltronics.qischeck.db.GradesContract.Grade;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * @author Christian
 *
 */
public class GradesContentProvider extends ContentProvider {
	
	public static final String TAG ="GradesProvider";

	private static final String MULTITYPE = "vnd.android.cursor.dir";
	private static final String ITEMTYPE  = "vnd.android.cursor.item";
	
	public static final int GRADES 				= 0;
	public static final int GRADE_BY_ID			= 1;
	public static final int CATEGORIES 			= 2;
	public static final int CATEGORY_BY_ID 		= 3;
	public static final int GRADES_BY_CATEGORY	= 4;
	
	private static final String GRADE_DIR_TYPE = MULTITYPE + "/" + "grade";
	private static final String GRADE_ITEM_TYPE = ITEMTYPE + "/" + "grade";  
	
	private static final String CATEGORY_DIR_TYPE = MULTITYPE + "/" + "category";
	private static final String CATEGORY_ITEM_TYPE = ITEMTYPE + "/" + "category"; 
	
	private static final UriMatcher uriMatcher;
	
	private SQLiteOpenHelper mOpenHelper;
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new GradesDBOpenHelper(getContext(), null);
		return true;
	}

	static { 
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher .addURI(GradesContract.AUTH, GradesContract.GRADES, GRADES);
		uriMatcher .addURI(GradesContract.AUTH, GradesContract.GRADES + "/#", GRADE_BY_ID);
		uriMatcher .addURI(GradesContract.AUTH, GradesContract.CATEGORIES, CATEGORIES);
		uriMatcher .addURI(GradesContract.AUTH, GradesContract.CATEGORIES + "/#", CATEGORY_BY_ID);
		uriMatcher .addURI(GradesContract.AUTH, GradesContract.CATEGORIES + "/#/" + GradesContract.GRADES, GRADES_BY_CATEGORY);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch(uriMatcher.match(uri)){
		case GRADES: 
			return GRADE_DIR_TYPE;
		
		case GRADE_BY_ID:
			return GRADE_ITEM_TYPE;
			
		case GRADES_BY_CATEGORY:
			return GRADE_DIR_TYPE;
			
		case CATEGORIES: 
			return CATEGORY_DIR_TYPE;
			
		case CATEGORY_BY_ID:
			return CATEGORY_ITEM_TYPE;
			
		default: return null;
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String table ;
		Uri changedUri;
		
		switch(uriMatcher.match(uri)){
		case GRADES: 
			table 	= DBContract.Table.GRADE;
			changedUri 	= Grade.BASE_URI ;
			break;
		case CATEGORIES:
			table 	= DBContract.Table.CATEGORY;
			changedUri 	= Category.BASE_URI;
			break;
		default: return null;
		}
		
		id = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		
		if(id != -1){
			getContext().getContentResolver().notifyChange(changedUri, null);
			return Uri.withAppendedPath(changedUri, String.valueOf(id));
		}else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String table = null;
		
		switch (uriMatcher.match(uri)) {
		case GRADES:
			table = DBContract.Table.GRADE;
			break;
		case GRADES_BY_CATEGORY:
			table = DBContract.Table.GRADE;
			break;
		case CATEGORIES:
			table = DBContract.Table.CATEGORY;
			break;
		case CATEGORY_BY_ID:
			table = DBContract.Table.CATEGORY;
			break;
		default:
			return null;
		}
		
		return db.query(table, projection, selection, selectionArgs, null, null, null);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
