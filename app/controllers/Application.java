package controllers;

import factories.mongo.*;
import com.mongodb.ServerAddress;
import play.*;
import play.mvc.*;
import java.util.List;

import views.html.*;

public class Application extends Controller {

    public static Result setConfig(String uri) {
    	String response = "";

    	try {
			DataAccess.forceNewInstance(uri);
			List<ServerAddress> serverList = DataAccess.mongo().getServerAddressList();

			for(ServerAddress serverAddress : serverList) {
				response += (response.length() == 0 ? "Success. Configured connections are: " : "; ") + serverAddress.getHost() + ":" + serverAddress.getPort();
			}
		}
		catch (Exception e) {
			response = e.getClass().getSimpleName() + ": " + e.getMessage();
		}

		return ok(response);
    }
}
