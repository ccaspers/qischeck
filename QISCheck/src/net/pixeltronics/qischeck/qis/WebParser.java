package net.pixeltronics.qischeck.qis;

import java.util.List;
import java.util.Map;

public interface WebParser {

	/**
	 * Parsed den HTML-Notenspiegel in eine Liste aus Key-Valuepaaren für Noten
	 * 
	 * @param html Notenspiegel
	 * @return Liste aus Noten
	 */
	public List<Map<String, String>> readGrades(String html);

	/**
	 * Parsed den HTML-Notenspiegel in eine Liste aus Key-Valuepaaren für Fächer
	 * 
	 * @param html Notenspiegel
	 * @return Liste aus Fächern
	 */
	public List<Map<String, String>> readCategories(String pageCache);

}