package ServerStuff;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import Project1to4Stuff.ThreadSafeInvertedIndex;
import Project1to4Stuff.WebCrawler;

//TODO seperate thread for input_end server

public class JettyServerClass {

	public static final int DEFAUL_PORT = 8080;
	
	public final int numPorts;

	public JettyServerClass() {
		this(DEFAUL_PORT);
	}
	
	public JettyServerClass(int numPorts){
		this.numPorts = numPorts;
	}
	
	//TODO pass in invertedIndex???
	@SuppressWarnings("unused")
	public void launch(){
		Server server = new Server(numPorts);
		
		String defaultSeed = "http://www.cs.usfca.edu/~cs212/htmlhelp/olist.html";
		ThreadSafeInvertedIndex index = new ThreadSafeInvertedIndex();
		WebCrawler fetcher = new WebCrawler();
		if(true){
			fetcher.crawlLink(defaultSeed, index);
			//fetcher.shutdown();
//			System.out.println("Finished building index from \n" + defaultSeed);
		}

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(new ServletHolder(new HomeServlet(index, fetcher)), "/");
		handler.addServletWithMapping(new ServletHolder(new ResultsServlet(index)), "/results");
		handler.addServletWithMapping(new ServletHolder(new JoshuaServlet(index)), "/joshua");
		handler.addServletWithMapping(new ServletHolder(new CookieConfigServlet()), "/config");
		handler.addServletWithMapping(new ServletHolder(new VisitTrackServlet()), "/track");
		
		server.setHandler(handler);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			System.err.println("Something went wrong with server");
		}
	}
}