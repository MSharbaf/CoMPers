package app.util;

import java.util.List;

import io.javalin.http.Context;

public class RequestUtil {

    public static String getQueryLocale(Context ctx) {
        return ctx.queryParam("locale");
    }

    public static String getParamResourceID(Context ctx) {
        return ctx.pathParam("resourceID");
    }

    public static String getParamProjectName(Context ctx) {
        return ctx.pathParam("projectName");
    }
    
    public static String getQueryResourceID(Context ctx) {
        return ctx.formParam("resourceID");
    }

    public static String getQueryProjectName(Context ctx) {
        return ctx.formParam("projectName");
    }
    
    public static String getQueryEcoreAddress(Context ctx) {
        return ctx.formParam("ecoreAddress");
    }
    
    public static String getQueryEnotationAddress(Context ctx) {
        return ctx.formParam("enotationAddress");
    }
    
    public static String getParamClientID(Context ctx) {
        return ctx.pathParam("clientID");
    }

    public static String getQueryClientID(Context ctx) {
        return ctx.formParam("clientID");
    }
    
    public static String getQuerySubmit(Context ctx) {
    	return ctx.formParam("Submit");
    }
    
    public static List<String> getQueryCDRule(Context ctx) {
    	return ctx.formParams("cdrule");
    }
    
    public static List<String> getQueryCPriority(Context ctx) {
    	return ctx.formParams("cPriority");
    }
    
    public static String getQueryRuleName(Context ctx) {
    	return ctx.formParam("ruleName");
    }
    
    public static String getQueryRuleFile(Context ctx) {
    	return ctx.formParam("ruleFile");
    }
    
    public static String getQueryResolutionStrategy(Context ctx) {
    	return ctx.formParam("resolutionStrategy");
    }
    
    public static String getQueryDetectionStrategy(Context ctx) {
    	return ctx.formParam("detectionStrategy");
    }
    
    public static String getQueryClientAddress(Context ctx) {
        return ctx.formParam("clientAddress");
    }
    
    public static String getQueryServerAddress(Context ctx) {
        return ctx.formParam("serverAddress");
    }
    
    public static String getQuerydefaultPublish(Context ctx) {
        return ctx.formParam("defaultPublish");
    }
    
    public static String getQuerydefaultSubscribe(Context ctx) {
        return ctx.formParam("defaultSubscribe");
    }
    
    public static String getQuerypublishStrategy(Context ctx) {
        return ctx.formParam("publishStrategy");
    }
    
    public static String getQuerysubscribeStrategy(Context ctx) {
        return ctx.formParam("subscribeStrategy");
    }
    
    public static String getQueryTime(Context ctx) {
        return ctx.formParam("time");
    }
    
    public static String getParamRole(Context ctx) {
        return ctx.pathParam("role");
    }
    
    public static String getQueryRole(Context ctx) {
        return ctx.formParam("role");
    }
    
    public static String getQueryFirstname(Context ctx) {
        return ctx.formParam("firstname");
    }
    
    public static String getQueryLastname(Context ctx) {
        return ctx.formParam("lastname");
    }
    
    public static String getParamUsername(Context ctx) {
        return ctx.pathParam("username");
    }
    
    public static String getQueryUsername(Context ctx) {
        return ctx.formParam("username");
    }
    
    public static String getQueryNewUsername(Context ctx) {
        return ctx.formParam("newusername");
    }

    public static String getQueryPassword(Context ctx) {
        return ctx.formParam("password");
    }

    public static String getQueryNewPassword(Context ctx) {
        return ctx.formParam("newpassword");
    }
    
    public static String getFormParamRedirect(Context ctx) {
        return ctx.formParam("loginRedirect");
    }

    public static String getSessionLocale(Context ctx) {
        return (String) ctx.sessionAttribute("locale");
    }

    public static String getSessionCurrentUser(Context ctx) {
        return (String) ctx.sessionAttribute("currentUser");
    }

    public static String getSessionCurrentRole(Context ctx) {
        return (String) ctx.sessionAttribute("currentRole");
    }
    
    public static boolean removeSessionAttrLoggedOut(Context ctx) {
        String loggedOut = ctx.sessionAttribute("loggedOut");
        ctx.sessionAttribute("loggedOut", null);
        return loggedOut != null;
    }

    public static String removeSessionAttrLoginRedirect(Context ctx) {
        String loginRedirect = ctx.sessionAttribute("loginRedirect");
        ctx.sessionAttribute("loginRedirect", null);
        return loginRedirect;
    }

}
