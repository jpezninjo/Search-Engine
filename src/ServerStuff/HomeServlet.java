package ServerStuff;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import Project1to4Stuff.SearchResult;
import Project1to4Stuff.ThreadSafeInvertedIndex;
import Project1to4Stuff.WebCrawler;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi



@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
	private static final String TITLE = "Messages";
	private static Logger log = Log.getRootLogger();
	private static ThreadSafeInvertedIndex index;
	
	
	private static HashMap<String, Integer> allResults = new HashMap<String, Integer>();
	
	private String lastQueryLine = null;

	private LinkedList<String> messages;
	private String newLink = null;
	
	public static final String VISIT_DATE = "Visited";
	public static final String VISIT_COUNT = "Count";
	public static final String PREV_SEARCHES = "Queries";
	public boolean partialSearch = true;

	public HomeServlet(ThreadSafeInvertedIndex index, WebCrawler fetcher) {
		super();
		HomeServlet.index = index;
		messages = new LinkedList<>();
	}

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
        out.printf("<head><title>%s</title>"
        		+ "<h1>CS212 Software Developement Fall2015 Project</h1>%n%n"
        		+ "</head>%n", TITLE);
//		out.printf("<body>%n");
		out.printf("<body style=\"background-color:green;\"%n");
//		out.printf("<h1>Message Board</h1>%n%n");

//		synchronized (messages) {
//			for (String message : messages) {
//				out.printf("<p>%s</p>%n%n", message);
//			}
//		}
		
		out.printf("<br><br><br>");
		printForm(request, response);
		
		
		if(!(newLink == null) && !newLink.isEmpty()){
			System.out.println("User requested  new URL to be added to index: " + newLink);
			WebCrawler newCrawler = new WebCrawler();
			if(newLink.startsWith("http") || newLink.startsWith("www")){
				out.printf("<br><strong>Sucessfully added new link into database</strong>");
				newCrawler.crawlLink(newLink, index);
			}else{
				out.printf("<br><strong>Please enter a valid link</strong>");
				System.out.println("Invalid url");
			}
		}
		
		String[] queries = null;
		
		if(!(lastQueryLine == null) && !(lastQueryLine.isEmpty())){
			if(partialSearch){
				queries = lastQueryLine.split(" ");
				System.out.println("Here are the latest queries being called: ");
	//			JOptionPane.showMessageDialog(null, queries);
				for(String query : queries){
					System.out.println(query);
					this.lastQueryLine += (" " + query);
					int value =  (allResults.get(query) == null ? 0 : (Integer)(allResults.get(query)) + 1);
					allResults.put(query, value);
					String[] partialS = {query};
//					System.out.println(query);
					long startTime = System.nanoTime();
					ArrayList<SearchResult> listOfSResults = index.partialSearch(partialS);
					double elapsedTime = (System.nanoTime() - startTime) / 1000000000.00;
					//elapsedTime = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
					if(listOfSResults.isEmpty()){
						out.printf("<br><strong>The phrase \"" + query + "\" could not be found in our database </strong>");
					}
					else{
						out.printf("<br><strong>The phrase \"" + query + "\" can be found in the following: </strong>");
						out.printf("Time to gather results: " + elapsedTime + "s");
					}
					
					for(SearchResult SResult : listOfSResults){
						locationToLink(SResult, request, response);
					}
				}
			} else{
				out.printf("<br><strong>Partial search was turned off for this request </strong>");
			}
		}else{
			out.printf("<br><strong>Please enter at least one word into the query box </strong>");
		}

//		printForm(request, response);
		
		
		
		Map<String, String> cookies = CookieBaseServlet.getCookieMap(request);

		String visitDate = cookies.get(VISIT_DATE);
		String visitCount = cookies.get(VISIT_COUNT);
		String cookiequeries = cookies.get(PREV_SEARCHES);
		if(cookiequeries == null){
			cookiequeries = "";
		}
			
		System.out.println("OLD cookie value= " + cookiequeries);
		System.out.println("ADDING-" + lastQueryLine);
		cookiequeries += " " + lastQueryLine;
		lastQueryLine = "";
		System.out.println("NEW cookie value= " + cookiequeries);

		out.printf("<p>");
		
		out.printf("<FORM>");
		out.printf("<INPUT TYPE=\"button\" onClick=\"history.go(0)\" VALUE=\"   Refresh   \">");
		out.printf("</FORM>");
		
		out.printf("<form method=\"get\" action=\"/config\">");
		out.printf("<button type=\"submit\">Clear Cookies</button>");
		out.printf("</form>");
		
		// Update visit count as necessary and output information.
		if ((visitDate == null) || (visitCount == null)) {
			visitCount = "0";

			out.printf("<String>You have never been to this webpage before! </Strong>");
			out.printf("Thank you for visiting.");
		}
		else {
			visitCount = Integer.toString(Integer.parseInt(visitCount) + 1);

			out.printf("You have visited this website %s times. ", visitCount);
			out.printf("Your last visit was on %s.", visitDate);
		}
		
		
		
//		System.out.println(cookiequeries);
//		if(cookiequeries == null){
//			cookiequeries = "";
//		}
//		if(!(lastQueryLine == null)){
////			String[] queries = lastQueryLine.split(" ");
//			System.out.println(cookiequeries);
//			for(String query : queries){
//				cookiequeries.concat(" " + query);
//			}
//		}
		
		if (request.getIntHeader("DNT") != 1) {
			response.addCookie(new Cookie("Visited", CookieBaseServlet.getLongDate()));
			response.addCookie(new Cookie("Count", visitCount));
			System.out.println("Adding cookie queries= " + cookiequeries);
			response.addCookie(new Cookie(PREV_SEARCHES, cookiequeries));
		}
		else {
			CookieBaseServlet.clearCookies(request, response);
			out.printf("<p>Your visits will not be tracked.</p>");
		}

		out.printf("</p>%n");
		
		
//		out.printf("<img src=\"http://imgur.com/MNagTEF\" style=\"width:304px;height:228px;\">");
		allResults = (HashMap<String, Integer>) sortByValue(allResults);
		int counter = 5, i = 0;
		Set<String> k = allResults.keySet();
		
//		out.printf("<p>Here's a result %s.</p>%n");

		out.printf("<p>This request was handled by thread %s.</p>%n",
				Thread.currentThread().getName());

		out.printf("<div class=\"footer\">Example new url: "
				+ "http://www.cs.usfca.edu/~sjengle/cs212/crawl/yellowthroat.html</div>");
		
		out.printf("<br><br><br><br><br><br><br><br><br>");
		out.printf("<br><br><br><br><br><br><br><br><br><br>");
		
		out.printf("%n");
		out.printf("<p style=\"font-size: 10pt; font-style: italic; text-align: center;");
		out.printf("border-top: 1px solid #eeeeee; margin-bottom: 1ex;\">");
		
		out.printf("<a href=\"http://imgur.com/MNagTEF\"><img src=\"http://i.imgur.com/MNagTEF.png\" title=\"source: imgur.com\" /></a>");
		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		
//		System.out.println("Parameters: " + request.getParameterMap());

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		this.lastQueryLine = request.getParameter("queryline");
		this.newLink  = request.getParameter("newlink");
		this.partialSearch = (request.getParameter("partialSearch") == null ? false : true);
		
		String message = request.getParameter("message");
		//username = username == null ? "anonymous" : username;
		message = message == null ? "" : message;

		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
//		username = StringEscapeUtils.escapeHtml4(username);
//		message = StringEscapeUtils.escapeHtml4(message);

		String formatted = String.format(
				"%s<br><font size=\"-2\">[ posted by %s at %s ]</font>",
				message, "anonymous", getDate());

		synchronized (messages) {
			messages.addLast(formatted);

			while (messages.size() > 2) {
			    String first = messages.removeFirst();
				log.info("Removing message: " + first);
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Query:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"queryline\" maxlength=\"50\" size=\"20\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Enter a new link into our data base:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"newlink\" maxlength=\"100\" size=\"60\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		
		out.printf("Partial Search?: <input type=\"checkbox\" checked=\"yes\" name=\"partialSearch\" value=\"true\"  /><br />");
		
		out.printf("<p><input type=\"submit\" value=\"Submit stuff\"></p>\n%n");
		out.printf("</form>\n%n");
	}

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
	
	private static void locationToLink(SearchResult e,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String link = e.getLocation();
		PrintWriter out = response.getWriter();
		out.printf("<br><a href=" + link + " + target=\"_blank\">" + link + "</a> (" +
		e.getFrequency() + " times)%n");
		
	}
	
    public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map )
{
    List<Map.Entry<K, V>> list =
        new LinkedList<Map.Entry<K, V>>( map.entrySet() );
    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
    {
        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
        {
            return (o1.getValue()).compareTo( o2.getValue() );
        }
    } );

    Map<K, V> result = new LinkedHashMap<K, V>();
    for (Map.Entry<K, V> entry : list)
    {
        result.put( entry.getKey(), entry.getValue() );
    }
    return result;
}
}