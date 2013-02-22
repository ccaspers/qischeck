package net.pixeltronics.qischeck.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.pixeltronics.qischeck.qis.MIParser;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

public class MIParserTest extends InstrumentationTestCase {
	
	private MIParser parser;
	private String testPage;

	protected void setUp() throws Exception {
		super.setUp();
		testPage = readPage();
		parser = new MIParser();
	}

	public void testReadGrades() {
		List<ContentValues> readGrades = parser.readGrades(testPage);
		assertEquals(34, readGrades.size());
		
	}

	public void testReadCategories() {
		List<ContentValues> cats = parser.readCategories(testPage);
		assertEquals(25, cats.size());
	}
	
	private String readPage() throws IOException{
		AssetManager assets = getInstrumentation().getContext().getAssets();
		InputStream stream = assets.open("grades.htm");
		return inputStream2String(stream);
	}
	
	/**
	 * Konvertiert einen InputStream in einen String
	 * @param instream
	 * @return String
	 * @throws IOException
	 */
	private String inputStream2String(InputStream instream)
			throws IOException {
		InputStreamReader inR = new InputStreamReader(instream);
		BufferedReader buf = new BufferedReader(inR);
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = buf.readLine()) != null) {
			builder.append(line);
		}
		return builder.toString();
	}

}
