package net.pixeltronics.qischeck.qis;

import java.util.List;

import android.content.ContentValues;

public interface WebParser {

	/**
	 * Parsed den HTML-Notenspiegel in eine Liste aus Key-Valuepaaren für Noten
	 * 
	 * @param html Notenspiegel
	 * @return Liste aus Noten
	 */
	public List<ContentValues> readGrades(String html);

	/**
	 * Parsed den HTML-Notenspiegel in eine Liste aus Key-Valuepaaren für Fächer
	 * 
	 * @param html Notenspiegel
	 * @return Liste aus Fächern
	 */
	public List<ContentValues> readCategories(String pageCache);

}