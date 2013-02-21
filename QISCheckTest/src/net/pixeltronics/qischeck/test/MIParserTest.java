package net.pixeltronics.qischeck.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import junit.framework.TestCase;
import net.pixeltronics.qischeck.MIParser;

public class MIParserTest extends TestCase {
	
	private MIParser parser;
	private String testPage;

	protected void setUp() throws Exception {
		super.setUp();
		URL resource = getClass().getResource("/net/pixeltronics/qischeck/test/resource/grades.htm");
		testPage = readPage(resource.getFile());
		parser = new MIParser();
	}

	public void testReadGrades() {
		List<Map<String,String>> readGrades = parser.readGrades(testPage);
		assertEquals(34, readGrades.size());
		
	}

	public void testReadCategories() {
		List<Map<String,String>> cats = parser.readCategories(testPage);
		assertEquals(25, cats.size());
	}
	
	private String readPage(String path) throws FileNotFoundException{
		return new Scanner(new File(path)).useDelimiter("\\Z").next();
	}

}
