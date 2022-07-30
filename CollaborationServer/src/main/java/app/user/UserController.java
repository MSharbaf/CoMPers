package app.user;

import org.mindrot.jbcrypt.BCrypt;

import app.util.Path;
import app.util.ViewUtil;
import io.javalin.http.Handler;

import static app.Main.*;
import static app.util.RequestUtil.*;

import java.util.Map;

public class UserController {

    // Authenticate the user by hashing the inputted password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public static boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.salt);
        return hashedPassword.equals(user.hashedPassword);
    }

    // This method doesn't do anything, it's just included as an example
    public static void setPassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
            // Update the user salt and password
            User u = userDao.getUserByUsername(username);
            u.setsalt(newSalt);
            u.sethashedPassword(newHashedPassword);
        }
    }
    
    
    public static Handler fetchAllUsers = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("users", userDao.getAllUsers());
        ctx.render(Path.Template.USERS_ALL, model);
    };

    public static Handler fetchOneUser = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("user", userDao.getUserByUsername(getParamUsername(ctx)));
        ctx.render(Path.Template.USERS_ONE, model);
    };
    
    public static Handler fetchOneUserforUpdate = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("user", userDao.getUserByUsername(getParamUsername(ctx)));
        ctx.render(Path.Template.UPDATEUSER, model);
    };
    
    public static Handler handleUpdateuserPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        String uID = getQueryUsername(ctx);
        String fname = getQueryFirstname(ctx);
        String lname = getQueryLastname(ctx); 
		String role = getQueryRole(ctx);

        User u = userDao.getUserByUsername(uID) ; 
        if(u != null) {
        	userDao.updateUserbyAdmin(uID, fname, lname, role);
        }
 
        model.put("registerationFailed", false);
        model.put("registerationSucceeded", true);
        model.put("user", userDao.getUserByUsername(getParamUsername(ctx)));
        ctx.render(Path.Template.UPDATEUSER, model);
    };
    
    
}
