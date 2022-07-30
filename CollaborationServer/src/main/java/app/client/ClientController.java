package app.client;

import io.javalin.http.Handler;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

import app.util.Path;
import app.util.ViewUtil;

import static app.Main.*;
import static app.util.RequestUtil.*;
import app.cmanagement.*;
import app.resource.*;

public class ClientController {

    public static Handler fetchAllClients = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("clients", clientDao.getAllClients());
        ctx.render(Path.Template.CLIENTS_ALL, model);
    };

    public static Handler fetchOneClient = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("client", clientDao.getClientByClientID(getParamClientID(ctx)));
        ctx.render(Path.Template.CLIENTS_ONE, model);
    };
    
    public static Handler fetchOneClientforUpdate = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        ClientInfo c = clientDao.getClientByClientID(getParamClientID(ctx)) ; 
        ArrayList<CDRule> resList = new ArrayList<CDRule>();
        for(Resource r: resourceDao.getAllResources()) {
        	Boolean check = false ; 
        	for(String st: c.resourceList) {
        		if(r.resourceID.equals(st)) {
        			check = true ; 
        			break ; 
        		}	
        	}
        	CDRule cdr = new CDRule(r.resourceID, r.creator, check);
        	resList.add(cdr);
        }

        model.put("client", c);
        model.put("resourceList", resList); 

        
//      model.put("client", clientDao.getClientByClientID(getParamClientID(ctx)));
//      model.put("resources", resourceDao.getAllResources()); 
        ctx.render(Path.Template.UPDATECLIENT, model);
    };
    
    public static Handler handleUpdateclientPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        String cID = getQueryClientID(ctx);
        String cAddress = getQueryClientAddress(ctx);
        String sAddress = getQueryServerAddress(ctx); 
		String defPub = getQuerydefaultPublish(ctx);
        String defSub = getQuerydefaultSubscribe(ctx);
        String time = getQueryTime(ctx);
       
        List<String> resourceList = getQueryCDRule(ctx);


        ClientInfo cl = clientDao.getClientByClientID(cID) ; 
        if(cl != null) {
        	clientDao.updateClient(cl, cAddress, sAddress, defPub, defSub, time, resourceList);
        }
 
        
        ArrayList<CDRule> resList = new ArrayList<CDRule>();
        for(Resource r: resourceDao.getAllResources()) {
        	Boolean check = false ; 
        	for(String st: cl.resourceList) {
        		if(r.resourceID.equals(st)) {
        			check = true ; 
        			break ; 
        		}	
        	}
        	CDRule cdr = new CDRule(r.resourceID, r.creator, check);
        	resList.add(cdr);
        }
        
        
        model.put("registerationFailed", false);
        model.put("registerationSucceeded", true);
        model.put("client", clientDao.getClientByClientID(getParamClientID(ctx)));
        model.put("resourceList", resList); 
        ctx.render(Path.Template.UPDATECLIENT, model);
    };
}
