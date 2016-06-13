package ServerStuff;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import Project1to4Stuff.ThreadSafeInvertedIndex;
import Project1to4Stuff.WebCrawler;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi

@SuppressWarnings("serial")
public class ResultsServlet extends HttpServlet {
	private static final String TITLE = "Results";
	private static Logger log = Log.getRootLogger();
	private static String image = "ProjGutenlogo";

	public ResultsServlet(ThreadSafeInvertedIndex index) {
		super();
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
        out.printf("<head><title>%s</title></head>%n", TITLE);
//		out.printf("<body background=\"" + image + "\">%n");
        out.printf("<body bgcolor=\"#E6E6FA\">");

		out.printf("<h1>CS212 Software Developement Fall2015 Project</h1>%n%n");
		out.printf("<h2>Results</h2>%n%n");
		
		System.out.println(request.getParameterNames());
		String url = request.getParameter("url");
		String queryLine = request.getParameter("query line");
		System.out.println("Results servlet-" + url);
		System.out.println("Results servlet-" + queryLine);
		
		out.printf("<br>");
		out.printf("<h6>Seed: " + url + " </h6>");
//		out.printf("<h6>Query: " + queryLine.split(" ") + " </h6>");
		
		printForm(request, response);


//		out.printf("<p>This request was handled by thread %s.</p>%n",
//				Thread.currentThread().getName());

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String url = request.getParameter("url");
		String query = request.getParameter("query line");
		
		if(ServletHelper.validateURL(url)){
			
			ThreadSafeInvertedIndex newIndex = new ThreadSafeInvertedIndex();
			
			WebCrawler newFetcher = new WebCrawler();
			newFetcher.crawlLink(url, newIndex);
			
		}


		// Avoid XSS attacks using Apache Commons StringUtils
		// Comment out if you don't have this library installed
//		username = StringEscapeUtils.escapeHtml4(username);
//		message = StringEscapeUtils.escapeHtml4(message);

		String formatted = String.format(
				"%s<br><font size=\"-2\">[ at %s ]</font>",
				url, getDate());


		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", "\results");
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Enter a URL:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"url\" maxlength=\"100\" size=\"60\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\" value=\"Ask\"></p>\n%n");
		out.printf("</form>\n%n");
	}

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}