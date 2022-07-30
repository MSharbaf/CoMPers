package app.profile;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;


import app.util.Path;
import app.util.ViewUtil;
import app.client.ClientInfo;
import app.user.*;
import app.propagation.Propagation;
import app.resource.Resource;

import static app.Main.*;
import static app.util.RequestUtil.*;


public class ProfileController {
    public static Handler serveProfilePage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", userDao.getAllUserNames());
        model.put("clients", clientDao.getAllClients());      
        ctx.render(Path.Template.PROFILE, model);
    };
    
    public static Handler serveAddclientPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("resources", resourceDao.getAllResources()); 
        model.put("clients", clientDao.getAllClients()); 
        ctx.render(Path.Template.ADDCLIENT, model);
    };

    public static Handler serveAdduserPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", userDao.getAllUsers());    
        ctx.render(Path.Template.ADDUSER, model);
    };
    
    public static Handler serveAddresourcePage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("resources", resourceDao.getAllResources());    
        ctx.render(Path.Template.ADDRESOURCE, model);
    };

    public static Handler servePropagatePage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("clients", clientDao.getAllClients());     
        model.put("resources", resourceDao.getAllResources()); 
        model.put("propagations", propagationDao.getAllPropagations());
        ctx.render(Path.Template.PROPAGATE, model);
    };
    
    public static Handler serveManageconflictPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("CManagement", cmanagementDao.getCManagement());
        ctx.render(Path.Template.MANAGECONFLICT, model);
    };    
    
    public static Handler handleAddclientPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        model.put("newClient", getQueryClientID(ctx));
        String defSub = getQuerydefaultSubscribe(ctx);
        String time = getQueryTime(ctx);
        
        List<String> resourceList = getQueryCDRule(ctx);

//        if(defSub.equals("OnSchedule"))
//        	defSub = defSub + " at " + t;
        ClientInfo c = new ClientInfo(getQueryClientID(ctx), getQueryClientAddress(ctx), getQueryServerAddress(ctx), 
        		getSessionCurrentUser(ctx), getQuerydefaultPublish(ctx), defSub, time, resourceList);
        clientDao.addClient(c); 
    	cmanagementDao.addNewClientPriority(c.clientID, 0);  
        model.put("registerationFailed", false);
        model.put("registerationSucceeded", true);
        ctx.render(Path.Template.ADDCLIENT, model);
    };
    
    public static Handler handleAdduserPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        String username = getQueryNewUsername(ctx);
        if(userDao.getUserByUsername(username)==null) {
            String firstname = getQueryFirstname(ctx);
            String lastname = getQueryLastname(ctx);
            String newPassword = getQueryNewPassword(ctx);
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newPassword, newSalt);
            String role = getQueryRole(ctx);

            User u = new User(username, firstname, lastname, role, newSalt,  newHashedPassword);
            userDao.addUser(u); 
            model.put("registerationFailed", false);
            model.put("registerationSucceeded", true);
        }
        	  
        ctx.render(Path.Template.ADDUSER, model);
    };
    
    public static Handler handleAddresourcePost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        String projectName = getQueryProjectName(ctx) ; 
        String ecoreAddress = ctx.uploadedFile("ecoreAddress").getFilename() ; 
        String enotationAddress = ctx.uploadedFile("enotationAddress").getFilename();
        Resource r = new Resource(getQueryResourceID(ctx), getQueryProjectName(ctx), ecoreAddress, enotationAddress, getSessionCurrentUser(ctx),LocalDateTime.now());
        resourceDao.addResource(r); 
        ctx.uploadedFiles("ecoreAddress").forEach(file -> {
            FileUtil.streamToFile(file.getContent(), "upload/" + projectName + "/model/" + file.getFilename());
        });
        ctx.uploadedFiles("enotationAddress").forEach(file -> {
            FileUtil.streamToFile(file.getContent(), "upload/" + projectName + "/model/" + file.getFilename());
        });
        model.put("registerationFailed", false);
        model.put("registerationSucceeded", true);
        ctx.render(Path.Template.ADDRESOURCE, model);
    };
    
    public static Handler handleAddpropagationPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        String rID = getQueryResourceID(ctx);
        String cID = getQueryClientID(ctx); 
        String setter = getSessionCurrentUser(ctx) ; 
        String pub = getQuerypublishStrategy(ctx) ; 
        String sub = getQuerysubscribeStrategy(ctx);
        String time = getQueryTime(ctx);
//        if(sub.equals("OnSchedule"))
//        	sub = sub + " at " + time;
        
        Propagation pro = propagationDao.getPropagation(rID, cID) ; 
        if(pro==null) {
        	Propagation p = new Propagation(getQueryClientID(ctx), getQueryResourceID(ctx), pub, sub, time, setter);
        	propagationDao.addPropagation(p); 
        }else {
        	propagationDao.updatePropagation(pro, pub, sub, time, setter);
        }
        
        model.put("registerationFailed", false);
        model.put("registerationSucceeded", true);
        model.put("clients", clientDao.getAllClients());     
        model.put("resources", resourceDao.getAllResources()); 
        model.put("propagations", propagationDao.getAllPropagations());
        ctx.render(Path.Template.PROPAGATE, model);

    };
    
}
