package ServerStuff;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Demonstrates how to create, use, and clear cookies. Vulnerable to attack
 * since cookie values are not sanitized prior to use!
 *
 * @see CookieBaseServlet
 * @see CookieIndexServlet
 * @see VisitTrackServlet
 */
@SuppressWarnings("serial")
public class VisitTrackServlet extends CookieBaseServlet {

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		log.info("GET " + request.getRequestURL().toString());

		prepareResponse("Configure", response);


		
		PrintWriter out = response.getWriter();
		out.printf("<META http-equiv=\"refresh\" content=\"1;URL=https://github.com/cs212/cs212-joperez-project\">");
		out.printf("%n");

		finishResponse(request, response);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		log.info("POST " + request.getRequestURL().toString());

		clearCookies(request, response);

		prepareResponse("Configure", response);

		PrintWriter out = response.getWriter();

		

		finishResponse(request, response);
	}
}
