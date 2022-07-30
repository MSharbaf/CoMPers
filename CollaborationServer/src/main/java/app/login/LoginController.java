package app.login;

import io.javalin.http.Handler;
import java.util.Map;

import app.user.UserController;
import app.util.Path;
import app.util.ViewUtil;

import static app.util.RequestUtil.*;
import static app.Main.*;

public class LoginController {

    public static Handler serveLoginPage = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("loggedOut", removeSessionAttrLoggedOut(ctx));
        model.put("loginRedirect", removeSessionAttrLoginRedirect(ctx));
        ctx.render(Path.Template.LOGIN, model);
    };

    public static Handler handleLoginPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        if (!UserController.authenticate(getQueryUsername(ctx), getQueryPassword(ctx))) {
            model.put("authenticationFailed", true);
            ctx.render(Path.Template.LOGIN, model);
        } else {
            ctx.sessionAttribute("currentUser", getQueryUsername(ctx));
            String role= userDao.getUserRoleByUsername(getQueryUsername(ctx));
            ctx.sessionAttribute("currentRole", role);
            model.put("authenticationSucceeded", true);
            model.put("currentUser", getQueryUsername(ctx));
            model.put("currentRole", role);
            if (getFormParamRedirect(ctx) != null) {
                ctx.redirect(getFormParamRedirect(ctx));
            }
            ctx.render(Path.Template.LOGIN, model);
//            TimeUnit.SECONDS.sleep(10);
            ctx.redirect(Path.Web.PROFILE);
        }
    };

    public static Handler handleLogoutPost = ctx -> {
        ctx.sessionAttribute("currentUser", null);
        ctx.sessionAttribute("currentRole", null);
        ctx.sessionAttribute("loggedOut", "true");
        ctx.redirect(Path.Web.LOGIN);
    };

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static Handler ensureLoginBeforeViewingClients = ctx -> {
        if (!ctx.path().startsWith("/clients")) {
            return;
        }
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect(Path.Web.LOGIN);
        }
    };
    
    
    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static Handler ensureLoginBeforeViewingProfiles = ctx -> {
        if (!ctx.path().startsWith("/profile")) {
            return;
        }
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect(Path.Web.LOGIN);
        }
    };
    
    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static Handler ensureLoginBeforeViewingResources = ctx -> {
        if (!ctx.path().startsWith("/resources")) {
            return;
        }
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect(Path.Web.LOGIN);
        }
    };

}
