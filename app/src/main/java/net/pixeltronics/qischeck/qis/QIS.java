package net.pixeltronics.qischeck.qis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import android.content.ContentValues;
import android.util.Log;

/**
 * Objekt zum Zugriff auf die Noten im QIS-System der Hochschule RheinMain
 * Anfragen werden an den Server per Apache HTTPClient gestellt
 * Der Notenspiegel wird mit JSoup geparsed
 * 
 * @author Christian Caspers
 *
 */
public class QIS {
	
	private static final String INPUT_USERNAME = "asdf";
	private static final String INPUT_PASSWORD = "fdsa";
	
	private static String LOGIN_URL  = "https://qis.hs-rm.de/qisserver/rds?state=user&type=1&category=auth.login&startpage=portal.vm&breadCrumbSource=portal";
	private static String ASI_URL    = "https://qis.hs-rm.de/qisserver/rds?state=change&type=1&moduleParameter=studyPOSMenu&nextdir=change&next=menu.vm&subdir=applications&xml=menu&purge=y&navigationPosition=functions%2CstudyPOSMenu&breadcrumb=studyPOSMenu&topitem=functions&subitem=studyPOSMenu";
	//private static String GRADES_URL = "https://qis.hs-rm.de/qisserver/rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=auswahlBaum&nodeID=auswahlBaum%7Cabschluss%3Aabschl%3D84%2Cstgnr%3D1%7Cstudiengang%3Astg%3DIDB&expand=0&asi=${ASI}";
	private static String GRADES_URL = "https://qis.hs-rm.de/qisserver/rds?state=notenspiegelStudent&next=list.vm&nextdir=qispos/notenspiegel/student&createInfos=Y&struct=auswahlBaum&nodeID=auswahlBaum%7Cabschluss%3Aabschl%3D90%2Cstgnr%3D1%7Cstudiengang%3Astg%3DIIM&expand=0&asi=${ASI}";
	private static String LOGOUT_URL = "https://qis.hs-rm.de/qisserver/rds?state=user&type=4&re=last&category=auth.logout&breadCrumbSource=&topitem=functions";

	private String username;
	private String password;
	private String asi;
	private DefaultHttpClient hc;
	private WebParser parser;
	private String pageCache;
	
	/**
	 * Erzeugt den HTTPClient des Parsers und legt die nötigen Informationen ab 
	 * 
	 * @param username
	 * @param password
	 */
	public QIS(String username, String password, WebParser parser){
		this.username = username;
		this.password = password;
		this.pageCache = null;
		this.hc = new DefaultHttpClient();
		this.parser = parser;
		hc.setRedirectHandler(new DefaultRedirectHandler() {
	        public boolean isRedirectRequested(HttpResponse response, HttpContext context)  {
	            boolean isRedirect=false;

                isRedirect = super.isRedirectRequested(response, context);

	            if (!isRedirect) {
	                int responseCode = response.getStatusLine().getStatusCode();
	                if (responseCode == 301 || responseCode == 302) {
	                    return true;
	                }
	            }
	            return isRedirect;
	        }
		  });
	}
	
	/**
	 * Stellt eine Anfrage an den Server, parsed den Notenspiegel und überführt
	 * ihn in eine Liste aus Key-Value-Paaren für Noten
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<ContentValues> getGrades() throws ClientProtocolException, IOException{
		String page = getPage();
		List<ContentValues> grades = parser.readGrades(page);
		return grades;
	}
	
	/**
	 * Stellt eine Anfrage an den Server, parsed den Notenspiegel und überführt
	 * ihn in eine Liste aus Key-Value-Paaren für Fächer
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<ContentValues> getCategories() throws ClientProtocolException, IOException{
		String page = getPage();
		return parser.readCategories(page);
	}

	private String getPage() throws ClientProtocolException, IOException {
		if(pageCache == null){
			String gradesUrl = GRADES_URL.replace("${ASI}", this.asi);
			pageCache = getPageContents(pageRequest(gradesUrl, null, true));
		}
		return pageCache;
	}

	/**
	 * Meldet den User am System an, der HTTPClient verwaltet die Session-Cookies automatisch
	 * Extrahiert die ASI aus der Seite, damit der Notenspiegel abgerufen werden kann
	 * 
	 * @throws ClientProtocolException wenn was dummes passiert (Keine Verbindung etc)
	 * @throws IOException wenn die Seite nicht gelesen werden kann
	 */
	public void login() throws ClientProtocolException, IOException{
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair(INPUT_USERNAME, this.username));
		formparams.add(new BasicNameValuePair(INPUT_PASSWORD, this.password));
		formparams.add(new BasicNameValuePair("submit","Anmelden"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		
		pageRequest(QIS.LOGIN_URL, entity,false);
		String page = getPageContents(pageRequest(ASI_URL, null, true));
		String[] asisplit = page.split("&amp;asi=",2);
		this.asi = asisplit[1].split("\"|&",2)[0];
		Log.v("QIS", "Asi bekommen: " +this.asi);
	}

	/**
	 * Ruft die Logout-URL auf um die Session zu beenden, mehr passiert eigentlich nicht
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void logout() throws ClientProtocolException, IOException{
		pageRequest(LOGOUT_URL, null, true);
		hc = null;
	}
	
	/**
	 * Extrahiert die Seite und wandelt sie in einen String um
	 * 
	 * @param response wird aus pageRequest() gewonnen
	 * @return HTML-Seite als String
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String getPageContents(HttpResponse response) throws IllegalStateException, IOException{
		String page ="";
		HttpEntity document = response.getEntity();
		System.out.println(response.getStatusLine().toString());
		if (document != null) {
			InputStream instream = document.getContent();
			try {
				page = inputStream2String(instream);
			} finally {
				instream.close();
			}
		}
		return page;
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
	
	/**
	 * Führt eine Anfrage aus, dabei können Formularelemente mitgesendet werden
	 * Wahlweise kann GET oder POST ausgeführt werden
	 * 
	 * @param url Seite die angefragt wird
	 * @param entity Elemente die geschickt werden sollen
	 * @param get True wenn GET sonst POST
	 * @return String mit Seite
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private HttpResponse pageRequest(String url, UrlEncodedFormEntity entity, Boolean get) throws ClientProtocolException, IOException {
		HttpResponse response;		
		if(get){
			HttpGet httpget = new HttpGet(url);
			response = hc.execute(httpget);
		}
		else{
			HttpPost httppost = new HttpPost(url);	
			httppost.setEntity(entity);
			response = hc.execute(httppost);
		}
		return response;
	}
	

}
