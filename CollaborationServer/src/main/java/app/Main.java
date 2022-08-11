package app;

import app.client.*;
import app.cmanagement.*;
import app.resource.*;
import app.user.*;
import app.propagation.*;
import app.index.IndexController;
import app.profile.ProfileController;
import app.login.LoginController;
import app.util.Filters;
import app.util.HerokuUtil;
import app.util.Path;
import app.util.SocketPacket;
import app.util.ViewUtil;
import app.util.WebSocketHandler;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.staticfiles.Location;
import java.io.IOException;
import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.net.URI;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.JSONObject;

import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

	

public class Main {

	public static Boolean localTest = false ; 

    // Declare dependencies
    public static ClientDao clientDao;
    public static UserDao userDao;
    public static ResourceDao resourceDao; 
    public static PropagationDao propagationDao; 
    public static CManagementDao cmanagementDao;
    public static ElementInfoDao elementInfoDao;
    public static ChangePropagationDao changePropagationDao;
    
//    public static ConflictCheckingThread conflictCheckThread ; 
    public static Boolean confThread; 
    private static int count = 0;
    

    public static void main(String[] args) throws IOException {

//      BasicConfigurator.configure();
//      Logger logger = Logger.getLogger(Main.class);
//      logger.info("Starting Server");
    	
    	
        // Instantiate your dependencies
        userDao = new UserDao();
        clientDao = new ClientDao(); 
        resourceDao = new ResourceDao(); 
        propagationDao = new PropagationDao(); 
        changePropagationDao = new ChangePropagationDao(); 
        cmanagementDao = new CManagementDao();
        elementInfoDao = new ElementInfoDao();
        confThread = true; 
        
//        ElementInfo element = new ElementInfo("17f10995-718b-4b76-8336-fd9e7f7ee692", "Seminar", "{\"location\":{\"x\":771.0,\"y\":123.0,\"eFlags\":0},\"containerId\":\"11cfbc26-6666-4223-a1e7-c668f2bcebb9\",\"elementTypeId\":\"node:enum\",\"args\":{},\"kind\":\"createNode\"}", "");
//        elementInfoDao.addElement(element);
        
//        CDRule cdr1 = new CDRule("Rule1", "RuleAddress1", false);
//        CDRule cdr2 = new CDRule("Rule2", "RuleAddress2", true); 
//        CDRule cdr3 = new CDRule("Rule3", "RuleAddress3", true);
//        ArrayList<CDRule> cdList = new ArrayList<>();
//        cdList.add(cdr1);
//        cdList.add(cdr2);
//        cdList.add(cdr3);
//        
//        CPriority cp1 = new CPriority("Client1", 1);
//        CPriority cp2 = new CPriority("Client2", 3);
//        CPriority cp3 = new CPriority("Client3", 5);
//        CPriority cp4 = new CPriority("Client4", 2);
//        CPriority cp5 = new CPriority("Client5", 4);
//        ArrayList<CPriority> cpList = new ArrayList<CPriority>();
//        cpList.add(cp1);
//        cpList.add(cp2);
//        cpList.add(cp3);
//        cpList.add(cp4);
//        cpList.add(cp5);
//        
//        cmanagementDao.updateCManagement("Highest Priority Wins", "Rule-Based Matching", cdList, cpList);
        
//        System.out.println("0) onClose Size is " + resourceDao.onCloseSize());
        
        userDao.readUsers();
        clientDao.readClients();
        resourceDao.readResources();
        propagationDao.readPropagations();
        cmanagementDao.readCManagement();
        elementInfoDao.readElements();
        
//        System.out.println("1) onClose Size is " + resourceDao.onCloseSize());
        
        
//        Client c1 = new Client("Client1", "127.0.0.1:3000", "127.0.0.1:8081", "Mohammadreza") ; 
//        Client c2 = new Client("Client2", "127.0.0.1:3001", "127.0.0.1:8082", "Bahman");
//        Client c3 = new Client("Client3", "127.0.0.1:3002", "127.0.0.1:8083", "Mohammadreza") ;
//        try {
//			clientDao.addClient(c1);
//	        clientDao.addClient(c2);
//	        clientDao.addClient(c3);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
//        User u1 = new User("Admin", "A", "Z", "admin", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEMLT83E1KDmUhCy");
//        User u2 = new User("Mohammadreza", "Mohammadreza", "Sharbaf", "designer", "$2a$10$h.dl5J86rGH7I8bD9bZeZe", "$2a$10$h.dl5J86rGH7I8bD9bZeZeci0pDt0.VwFTGujlnEaZXPf/q7vM5wO");
//        User u3 = new User("Bahman", "Bahman", "Zamani", "designer", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEMLT83E1KDmUhCy");
//        User u4 = new User("Gerson", "Gerson", "Sunye", "designer", "$2a$10$E3DgchtVry3qlYlzJCsyxe", "$2a$10$E3DgchtVry3qlYlzJCsyxeSK0fftK4v0ynetVCuDdxGVl1obL.ln2");
//        try {
//			userDao.addClient(u1);
//	        userDao.addClient(u2);
//	        userDao.addClient(u3);
//	        userDao.addClient(u4);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
  		
//        Resource r = new Resource("Resources1", "SharbafTest", "Test.ecore", "Test.enotation", "Mohammadreza", LocalDateTime.now());
//        resourceDao.addResource(r);

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH);
            config.registerPlugin(new RouteOverviewPlugin("/routes"));
        }).start(HerokuUtil.getHerokuAssignedPort());

        app.routes(() -> {
            before(Filters.handleLocaleChange);
            before(LoginController.ensureLoginBeforeViewingClients);
            before(LoginController.ensureLoginBeforeViewingProfiles);
            before(LoginController.ensureLoginBeforeViewingResources);
            get(Path.Web.INDEX, IndexController.serveIndexPage);
            get(Path.Web.CLIENTS, ClientController.fetchAllClients); 
            get(Path.Web.ONE_CLIENT, ClientController.fetchOneClient); 
            get(Path.Web.UPDATECLIENT, ClientController.fetchOneClientforUpdate); 
            get(Path.Web.USERS, UserController.fetchAllUsers); 
            get(Path.Web.ONE_USER, UserController.fetchOneUser); 
            get(Path.Web.UPDATEUSER, UserController.fetchOneUserforUpdate);
            get(Path.Web.RESOURCES, ResourceController.fetchAllResources); 
            get(Path.Web.ONE_RESOURCE, ResourceController.fetchOneResource); 
            get(Path.Web.PROFILE, ProfileController.serveProfilePage);
            get(Path.Web.ADDCLIENT, ProfileController.serveAddclientPage);        
            get(Path.Web.ADDUSER, ProfileController.serveAdduserPage);        
            get(Path.Web.PROPAGATE, ProfileController.servePropagatePage);
            get(Path.Web.MANAGECONFLICT, ProfileController.serveManageconflictPage);
            get(Path.Web.ADDRESOURCE, ProfileController.serveAddresourcePage);
            get(Path.Web.LOGIN, LoginController.serveLoginPage);
            post(Path.Web.ADDCLIENT, ProfileController.handleAddclientPost); 
            post(Path.Web.ADDUSER, ProfileController.handleAdduserPost); 
            post(Path.Web.UPDATECLIENT, ClientController.handleUpdateclientPost); 
            post(Path.Web.UPDATEUSER, UserController.handleUpdateuserPost); 
            post(Path.Web.ADDRESOURCE, ProfileController.handleAddresourcePost); 
            post(Path.Web.PROPAGATE, ProfileController.handleAddpropagationPost); 
            post(Path.Web.MANAGECONFLICT, CManagementController.handleSaveManageconflictPost); 
            post(Path.Web.LOGIN, LoginController.handleLoginPost);
            post(Path.Web.LOGOUT, LoginController.handleLogoutPost);
        });

        app.error(404, ViewUtil.notFound);
        
        
        
        app.ws("/websocket", ws -> {

            ws.onConnect((ctx) -> {
//            	System.out.println("A new client is Connected");
            	System.out.println("Wellcome to Server --- Client: " + ctx.session.getRemoteAddress().toString()); 
//            	session.getRemote().sendString("Server Address: " + app.jettyServer().getServerHost() + ":" + app.jettyServer().getServerPort());
            });

            ws.onMessage((ctx) -> {
            	if(ctx.message().isEmpty())
            		System.out.println("Message is Null");
            	
//            	logger.debug( String.format("Received Message: " + message.message()));
                System.out.println("Reciieved Message in the Server is: " + ctx.message());
                
                if(ctx.message().charAt(0)=='{') 
                	receiveOnlineChange(ctx.message());
//            	callSocket(ctx.message());
                
                else if(ctx.message().charAt(0)=='O') {
//                	System.out.println(ctx.message());
                	receiveOfflineChange(ctx.message());
                }
                else if(ctx.message().charAt(0)=='C') {
                	sendClientInfo(ctx.message());
                }
                else if(ctx.message().charAt(0)=='T') {
//                	System.out.println(ctx.message());
                	receiveCloseEditor(ctx.message());
                }
                /***************************************************************************/
                	
//            	ctx.session.getRemote().sendString("Echo: " + ctx.message());
            });
            
            ws.onClose(ctx -> System.out.println("Cloosed :" + ctx.session.getRemoteAddress().toString()));

            ws.onError(ctx -> System.out.println("Erroored"));
            
        });
        
    }
    
    public static void receiveOnlineChange(String messageReceived) {
		System.out.println("[Server-->Main-->receiveOnlineChange] Receive Online Change Startted ... "); 
        
		if(messageReceived.charAt(0)!='{')
			return ; 
		
		SocketPacket sp = new Gson().fromJson(messageReceived, SocketPacket.class); 
		
		sp.setServerTime(++count);
		
		changePropagationDao.addOperationToQueue(sp);
		
		System.out.println("[Server-->Main-->receiveOnlineChange] confThread is " + confThread);
		if(confThread==true) {
			SocketPacket tsp = Main.changePropagationDao.pickHeadOfOperationQueue(sp.getResourceID());
			new ConflictCheckingThread(tsp); 
		}
		
//		if(conflictCheckThread.getResourceID().equals(sp.getResourceID()))
//			if(conflictCheckThread.getFlag()==false)		
//				conflictCheckThread = new ConflictCheckingThread(sp.getResourceID()); 
    }
    
    public static void receiveOfflineChange(String messageReceived) {
		System.out.println("[Server-->Main] Receive Offline Change Startted ... "); 
        
		int i = messageReceived.indexOf("Operation");
		System.out.println("i is: " + i);
		messageReceived = messageReceived.substring(9);
		
		SocketPacket[] operationList = new Gson().fromJson(messageReceived, SocketPacket[].class);
		System.out.println("Resource count is: " + operationList.length);
		
		String resID =  operationList[0].getResourceID() ; 
		System.out.println("First Resource is: " + operationList[0].getResourceID());	
		
		++count ; 
		for(SocketPacket sp: operationList) {
			sp.setServerTime(count);
			changePropagationDao.addOperationToQueue(sp);
		}	
		
//		if(conflictCheckThread.getResourceID().equals(resID) && conflictCheckThread.isAlive()==false)		
//			conflictCheckThread = new ConflictCheckingThread(resID); 
		
		System.out.println("[Server-->Main-->receiveOfflineChange] confThread is " + confThread);
		if(confThread==true) {
			SocketPacket tsp = Main.changePropagationDao.pickHeadOfOperationQueue(resID);
			new ConflictCheckingThread(tsp); 
    	}
		
    }

    public static void receiveCloseEditor(String messageReceived) {
		System.out.println("[Server-->Main] Receive Close Editor Startted ... "); 
        
		int i = messageReceived.indexOf("TrueClose");
		System.out.println("i is: " + i);
		messageReceived = messageReceived.substring(9);
		
		JSONObject jsonReceived = new JSONObject(messageReceived.toString());
		
    	String clientID = (String) jsonReceived.get("clientID");
		String resourceID = (String) jsonReceived.get("resourceID"); 
//		String operationReceived = (String) jsonReceived.get("operation");
		
		System.out.println("[Server-->Main] Received clientID: " + clientID);
		System.out.println("[Server-->Main] Received resourceID: " + resourceID);
		
		resourceDao.updateOnClose(clientID, resourceID, true) ; 
		
		
		System.out.println("resourceDao.isTrueOnClose(resourceID) is " + resourceDao.isTrueOnClose(resourceID));
		if(changePropagationDao.getSizeOfOnClosedList(resourceID)>0)
			System.out.println("changePropagationDao.getSizeOfOnClosedList(resourceID)>0 is True"); 
		else
			System.out.println("changePropagationDao.getSizeOfOnClosedList(resourceID)>0 is False"); 
			
		
		if(resourceDao.isTrueOnClose(resourceID) && changePropagationDao.getSizeOfOnClosedList(resourceID)>0) {
			String json = new Gson().toJson(changePropagationDao.getAllOperationsOfOnClosedList(resourceID));
	        String socketPackets = "OnClose" + json ;         
	        System.out.println("OnClose SocketPackets are: " + socketPackets);
	        
	        
			new SendOnCloseChangeThread(resourceID, socketPackets);
			
			changePropagationDao.removeAllOperationOfOnClosedList(resourceID);
		}
    }
    
	public static void sendClientInfo(String messageReceived) {
		System.out.println("[Server-->Main] Send Client Info Startted ... "); 
		
		messageReceived = messageReceived.substring(1);
		System.out.println("[Server-->Main] Received message is: " + messageReceived);
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		System.out.println("[Server-->Main] After Sleep ... "); 
		
		JSONObject jsonReceived = new JSONObject(messageReceived.toString());
				
    	String clientID = (String) jsonReceived.get("clientID");
		String resourceID = (String) jsonReceived.get("resourceID"); 
//		String operationReceived = (String) jsonReceived.get("operation");
		
		System.out.println("[Server-->Main] Received clientID: " + clientID);
		System.out.println("[Server-->Main] Received resourceID: " + resourceID);
		
		for(Resource r : resourceDao.getAllResources())
			if(resourceID.contains(r.projectName) && resourceID.contains(r.ecoreAddress)) {
				resourceID = r.resourceID; 
				break ; 
			}
		
		System.out.println("[Server-->Main] Determined resourceID: " + resourceID);
		
//		System.out.println("[Server-->Main] Received Operation: " + operationReceived);
		
//    	JSONObject jsonSend = new JSONObject().put("clientID", "Server").put("resourceID", resourceIDReceived).put("operation", operationReceived);
    	
//    	String messageSend = jsonSend.toString(); 
//    	System.out.println("[Server-->Main] JSON String: " + messageSend); 

//    	System.out.println("[Server-->Main] JSON String: " + jsonReceived.toString()); 

		
        String json = new Gson().toJson(clientDao.getClientByClientID(clientID));
        String info = "Info" + json ; 
        System.out.println("Info is: " + info);
        
        
        Iterable<Propagation> Pro = propagationDao.getPropagationsByclientID(clientID);
        json = new Gson().toJson(Pro);
        String propagations = "Propagate" + json ;         
        System.out.println("Propagations are: " + propagations);
                
        
        List<Resource> Res = new ArrayList<Resource>();
        for(String resID : clientDao.getClientByClientID(clientID).getResourceList()) {
        	Res.add(resourceDao.getResourceByresourceID(resID));
        }
        json = new Gson().toJson(Res);
        String resources = "Resources" + json ; 
        System.out.println("Resources are: " + resources);
		
        
        // Set OnClose False
        Boolean flagOnClose = true; 
    	for(Propagation p : Pro)
    		if(p.resourceID.equals(resourceID)) {
				flagOnClose = false ;
    			if(p.getpublishStrategy()==null)
    				flagOnClose = true ; 
    			else if(p.publishStrategy.equals("Online")) {
    				resourceDao.updateOnClose(clientID, resourceID, false);
    				System.out.println("On close Propagation for " + clientID + " and " + resourceID + " set false ... ");
    			}
    			break; 
    		}
        if(flagOnClose) {
        	if(clientDao.getClientByClientID(clientID).defaultPublish.equals("Online")) {
        		resourceDao.updateOnClose(clientID, resourceID, false);
				System.out.println("On close default Propagation for " + clientID + " set false ... ");
        	}
        }
        
        
		// Client protocol to be used
		HttpClient httpClient = new HttpClient();

      	// Create a Jetty WebSocket client
      	WebSocketClient webSocketClient = new WebSocketClient(httpClient);

      	// Our application handler to respond to socket events
      	WebSocketHandler webSocketHandler = new WebSocketHandler();

      	try{
//    	  	String destUri = "ws://localhost:8082/websocket";
//    	  	String destUri = "ws://localhost:5007";
//      	String destUri = "ws://localhost:6928/websocket";
      		
      		String destUri = null ; 
      		
      		
      		if(localTest==true) {
	      		if(clientID.equals("Client1"))
	      			destUri = "ws://localhost:6001/websocket";
	      		else if(clientID.equals("Client3"))
	      			destUri = "ws://localhost:6003/websocket";
      		}
      		else {
      			String clientAddress = clientDao.getClientByClientID(clientID).getclientAddress();
      			clientAddress = clientAddress.substring(0, clientAddress.indexOf(':'));      			
      			destUri = "ws://" + clientAddress + ":6000/websocket";
      			System.out.println("destUri is " + destUri);
      		}
//      		if(clientID.equals("Client1"))
//      			destUri = "ws://192.168.1.3:6000/websocket";
//      		else if(clientID.equals("Client2"))
//      			destUri = "ws://192.168.1.5:6000/websocket";
//      		else if(clientID.equals("Client3"))  
//      			destUri = "ws://192.168.1.7:6000/websocket";


          	webSocketClient.start();
          	URI echoUri = new URI(destUri);
          	ClientUpgradeRequest request = new ClientUpgradeRequest();

          
          	// The seeion can be used to gracefully close the connection from the client side.
          	// The example WebSocket server closes the current WebSocket after replying so we dont
          	// need it in this example.
          	Session session = webSocketClient.connect(webSocketHandler, echoUri, request).get();
          
          
          	// Send Operation to another client 
          	session.getRemote().sendString(info);
          	session.getRemote().sendString(resources);
          	session.getRemote().sendString(propagations);
//          	session.getRemote().sendString(jsonReceived.toString());

          	// Connection information
          	System.out.println("[Server-->Main] Connecting to: " + echoUri);

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
