package app.propagation;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.google.gson.Gson;

import app.Main;
import app.util.SocketPacket;
import app.util.WebSocketHandler;


public class SendOneChangeThread implements Runnable  {

	   Thread t;
	   SocketPacket tsp ; 
	   String clientID;
	   
	   public SendOneChangeThread(String cID, SocketPacket sp) {
	    
	      // thread created
	      t = new Thread(this, "Send Change Thread");
	     
	      tsp = sp ; 
	      clientID = cID;
	      // prints thread created
	      System.out.println("thread  = " + t);
	      
	      // this will call run() function
	      System.out.println("Calling run() function... ");
	      
	      t.start();
	   }

	   public void run() {
	      System.out.println("Inside run()function => Before");
	      sendSocket(clientID, tsp);
	      System.out.println("Inside run()function => After");	      
	   }
	   
	   
	   
	   public void sendSocket(String clientID, SocketPacket sp) {
		   System.out.println("[Server-->Main] Call Socket Startted ... "); 
			
			// Client protocol to be used
		   HttpClient httpClient = new HttpClient();

	      	// Create a Jetty WebSocket client
		   WebSocketClient webSocketClient = new WebSocketClient(httpClient);

	      	// Our application handler to respond to socket events
		   WebSocketHandler webSocketHandler = new WebSocketHandler();

		   try{	      		
			   String destUri = null ; 
	      		
			   String serverAddress = Main.clientDao.getClientByClientID(clientID).serverAddress; 
			   serverAddress = serverAddress.substring(0, serverAddress.indexOf(':'));
			   destUri = "ws://" + serverAddress + ":6000/websocket" ;
			   System.out.println("destUri for " + clientID + " is : " + destUri);
	      		
			   if(Main.localTest==true) {   
				   if(clientID.equals("Client1")) {
					   destUri = "ws://localhost:6001/websocket";
					   System.out.println("send to Client1 on 6001");
				   }
				   else if(clientID.equals("Client3")) {
					   destUri = "ws://localhost:6003/websocket";
					   System.out.println("send to Client3 on 6003");
				   }
			   }
			   else {
				   String clientAddress = Main.clientDao.getClientByClientID(clientID).getclientAddress();
				   clientAddress = clientAddress.substring(0, clientAddress.indexOf(':'));      			
				   destUri = "ws://" + clientAddress + ":6000/websocket";
				   System.out.println("destUri is " + destUri);
			   }
//	      		if(clientID.equals("Client1"))
//	      			destUri = "ws://192.168.1.3:6000/websocket";
//	      		else if(clientID.equals("Client2"))
//	      			destUri = "ws://192.168.1.5:6000/websocket";
//	      		else if(clientID.equals("Client3"))
//	      			destUri = "ws://192.168.1.7:6000/websocket";

			   webSocketClient.start();
			   URI echoUri = new URI(destUri);
			   ClientUpgradeRequest request = new ClientUpgradeRequest();

	          
	          	// The session can be used to gracefully close the connection from the client side.
	          	// The example WebSocket server closes the current WebSocket after replying so we don't need it in this example.
			   Session session = webSocketClient.connect(webSocketHandler, echoUri, request).get();
	          
	          
	          	// Send Operation to another client 
			   System.out.println("[Server-->SendOneChangeThread-->sendSocket] Before Creation of GSON Json String"); 	        
			   String json = new Gson().toJson(sp);		        
			   session.getRemote().sendString(json.toString());

	          	// Connection information
			   System.out.println("[Server-->Main] sending to: " + echoUri + "is Done ....!");

			   webSocketHandler.awaitClose(5, TimeUnit.SECONDS);
	          
			   System.out.println("[Server-->Main] Client Address: " + session.getLocalAddress().getAddress().toString() + ":" + session.getLocalAddress().getPort());
			   System.out.println("[Server-->Main] Call Socket Finished ... "); 
		   }
		   catch(Throwable t) {
	      		System.out.println("[Server-->Main] WebSocket failed: " + t);
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
