package org.eclipse.emfcloud.ecore.glsp.handler;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.emfcloud.ecore.glsp.EcoreServerLauncher;
import org.eclipse.emfcloud.ecore.glsp.util.SocketPacket;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.google.gson.Gson;


public class SendChangeOfflineThread implements Runnable  {

	   Thread t;
	   List<SocketPacket> tspList ; 
	   
	   public SendChangeOfflineThread(List<SocketPacket> spList) {
	    
	      // thread created
	      t = new Thread(this, "Send Change Offline Thread");
	     
	      tspList = spList ; 
	      // prints thread created
	      System.out.println("thread  = " + t);
	      
	      // this will call run() function
	      System.out.println("Calling run() function... ");
	      
	      t.start();
	   }

	   public void run() {
	      System.out.println("Inside run()function => Before");
	      sendSocket(tspList);
	      System.out.println("Inside run()function => After");	      
	   }
	   
	   
		public void sendSocket(List<SocketPacket> spList) {
			System.out.println("[Client1-->ThreadSocket-->sendSocket] Call Socket Startted ... "); 
			
	        // Client protocol to be used
			HttpClient httpClient = new HttpClient();
	      
		    // Create a Jetty WebSocket client
			WebSocketClient webSocketClient = new WebSocketClient(httpClient);
		
		    // Our application handler to respond to socket events
	    	WebSocketHandler webSocketHandler = new WebSocketHandler();
		
		    try{
//		    	String destUri = "ws://localhost:7000/websocket";
		    	String destUri = "ws://" + EcoreServerLauncher.changePropagation.getServerAddress() + ":7000/websocket";

		    	
	    	  
		    	webSocketClient.start();
	         
		    	System.out.println("[Client1-->ThreadSocket-->sendSocket] Web Socket Startted ... "); 
		    	
		    	URI echoUri = new URI(destUri);
		    	ClientUpgradeRequest request = new ClientUpgradeRequest();

		    	// The seeion can be used to gracefully close the connection from the client side.
		    	// The example WebSocket server closes the current WebSocket after replying so we dont
		    	// need it in this example.
		    	Session session = webSocketClient.connect(webSocketHandler, echoUri, request).get();
		    	
//		    	System.out.println("[Client1-->ChangePropagation-->sendSocket] Before Creation of GSON Operation String"); 
		    	
//		        String op = new Gson().toJson(operation);
//		        String resourceID = modelState.getClientId().toString() ; 
//		        SocketPacket sp = new SocketPacket(clientID, resourceID, elementID, operation, DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date()), 0);

		    	System.out.println("[Client1-->ChangePropagation-->sendSocket] Before Creation of GSON Json String"); 	        
		        String json = new Gson().toJson(spList);
		        
		        json = "Operation" + json ; 
		        
//		    	System.out.println("[Client1-->EcoreOperation-->sendSocket] Before Creation of JSON String ");    	
//		    	JSONObject json = new JSONObject().put("clientID", clientID.toString()).put("elemenID", elementID.toString()).put("resourceID", modelState.getClientId().toString()).put("operation", op.toString());

		    	
		    	System.out.println("[Client1-->ThreadSocket-->sendSocket] JSON String: " + json.toString()); 
	      	  	session.getRemote().sendString(json.toString());
	 
	      	  	// Connection information
	      	  	System.out.println("[Client1-->ThreadSocket-->sendSocket] Connecting to: " + echoUri);

	          	webSocketHandler.awaitClose(5, TimeUnit.SECONDS);
	          
	          	System.out.println("[Client1-->ThreadSocket-->sendSocket] Client Address: " + session.getLocalAddress().getAddress().toString() + ":" + session.getLocalAddress().getPort());
	  			System.out.println("[Client1-->ThreadSocket-->sendSocket] Call Socket Finished ... "); 
	  			
	  			
		    }
		    catch(Throwable t) {
		    	System.out.println("[Client1-->ChangePropagation-->sendSocket] WebSocket failed: " + t);
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
