package net.pixeltronics.qischeck.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class GradesDBOpenHelper extends SQLiteOpenHelper {

	private static final String DROP = "DROP TABLE IF EXISTS ";

	public GradesDBOpenHelper(Context context,CursorFactory factory) {
		super(context, DBContract.DBName, factory, DBContract.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBContract.Table.Category.CREATE);
		db.execSQL(DBContract.Table.Grade.CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP + DBContract.Table.CATEGORY);
		db.execSQL(DROP + DBContract.Table.GRADE);
	}

}
