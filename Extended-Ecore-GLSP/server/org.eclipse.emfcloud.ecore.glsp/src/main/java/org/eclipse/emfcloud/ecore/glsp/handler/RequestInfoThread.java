package org.eclipse.emfcloud.ecore.glsp.handler;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.emfcloud.ecore.glsp.EcoreServerLauncher;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONObject;

import com.google.gson.Gson;


public class RequestInfoThread implements Runnable  {

	   Thread t;
	   String cID; 
	   String rID;
	   
	   RequestInfoThread(String clientID, String resourceID) {
	    
	      // thread created
	      t = new Thread(this, "Send Change Thread");
	     
	      cID = clientID ; 
	      rID = resourceID;
	      // prints thread created
	      System.out.println("thread  = " + t);
	      
	      // this will call run() function
	      System.out.println("Calling run() function... ");
	      
	      t.start();
	   }

	   public void run() {
	      System.out.println("Inside run()function => Before");
	      requestClientInfo(cID, rID);
	      System.out.println("Inside run()function => After");	      
	   }
	   
	   
	   private void requestClientInfo(String clientID, String resourceID) {
			System.out.println("[Client1-->ChangePropagation-->requestClientInfo] Call Socket Startted ... "); 
			
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			HttpClient httpClient = new HttpClient();
			WebSocketClient webSocketClient = new WebSocketClient(httpClient);	
	    	WebSocketHandler webSocketHandler = new WebSocketHandler();
		
		    try{
//		    	String destUri = "ws://192.168.1.1:7000/websocket";
//		    	String destUri = "ws://192.168.1.5:7000/websocket";
//		    	String destUri = "ws://localhost:7000/websocket";
		    	String destUri = "ws://" + EcoreServerLauncher.changePropagation.getServerAddress() + ":7000/websocket";

		    	
		    	webSocketClient.start();
	         
		    	System.out.println("[Client1-->ChangePropagation-->requestClientInfo] Web Socket Startted ... "); 
		    	
		    	URI echoUri = new URI(destUri);
		    	ClientUpgradeRequest request = new ClientUpgradeRequest();

		    	Session session = webSocketClient.connect(webSocketHandler, echoUri, request).get();
		    	
		    	System.out.println("[Client1-->ChangePropagation-->requestClientInfo] Before Sending ClientID"); 
		    	
		    	JSONObject json = new JSONObject().put("clientID", clientID.toString()).put("resourceID", resourceID.toString());
		    	session.getRemote().sendString("C" + json.toString());
//	      	  	session.getRemote().sendString(clientID + "+" + resourceID);
	 
	      	  	// Connection information
	      	  	System.out.println("[Client1-->ChangePropagation-->requestClientInfo] Connecting to: " + echoUri);

	          	webSocketHandler.awaitClose(5, TimeUnit.SECONDS);
	          
	          	System.out.println("[Client1-->ChangePropagation-->requestClientInfo] Client Address: " + session.getLocalAddress().getAddress().toString() + ":" + session.getLocalAddress().getPort());
	  			System.out.println("[Client1-->ChangePropagation-->requestClientInfo] Call Socket Finished ... "); 
	  		
		    }
		    catch(Throwable t) {
		    	System.out.println("[Client1-->ChangePropagation-->requestClientInfo] WebSocket failed: " + t);
		    }      
		    finally {
		    	try {
					webSocketClient.stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
	    }
	    
}
