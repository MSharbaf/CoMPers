package app.util;

public class Path {

    public static class Web {
        public static final String INDEX = "/index";
        public static final String LOGIN = "/login";
        public static final String PROFILE = "/profile";
        public static final String ADDCLIENT = "/profile/addclient";
        public static final String ADDUSER = "/profile/adduser";
        public static final String PROPAGATE = "/profile/propagate";
        public static final String MANAGECONFLICT = "/profile/manageconflict";
        public static final String ADDRESOURCE = "/profile/addresource";
        public static final String LOGOUT = "/logout";
        public static final String CLIENTS = "/clients";
        public static final String ONE_CLIENT = "/clients/{clientID}";
        public static final String UPDATECLIENT = "/clients/{clientID}/update";
        public static final String USERS = "/users";
        public static final String ONE_USER = "/users/{username}";
        public static final String UPDATEUSER = "/users/{username}/update";
        public static final String RESOURCES = "/resources";
        public static final String ONE_RESOURCE = "/resources/{resourceID}";

    }

    public static class Template {
        public static final String INDEX = "/velocity/index/index.vm";
        public static final String LOGIN = "/velocity/login/login.vm";
        public static final String PROFILE = "/velocity/profile/profile.vm";
        public static final String ADDCLIENT = "/velocity/profile/addclient.vm";
        public static final String ADDUSER = "/velocity/profile/adduser.vm";
        public static final String PROPAGATE = "/velocity/profile/propagate.vm";
        public static final String MANAGECONFLICT = "/velocity/profile/manageconflict.vm";
        public static final String ADDRESOURCE = "/velocity/profile/addresource.vm";
        public static final String CLIENTS_ALL = "/velocity/client/all.vm";
        public static final String CLIENTS_ONE = "/velocity/client/one.vm";
        public static final String UPDATECLIENT = "/velocity/client/update.vm";
        public static final String USERS_ALL = "/velocity/user/all.vm";
        public static final String USERS_ONE = "/velocity/user/one.vm";
        public static final String UPDATEUSER = "/velocity/user/update.vm";
        public static final String RESOURCES_ALL = "/velocity/resource/all.vm";
        public static final String RESOURCES_ONE = "/velocity/resource/one.vm";
        public static final String NOT_FOUND = "/velocity/notFound.vm";
    }

}
