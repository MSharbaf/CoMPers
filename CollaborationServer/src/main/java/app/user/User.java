package app.user;

public class User {
    public final String username;
    public String firstname;
    public String lastname;
    public String role;
    public String salt;
    public String hashedPassword;

    public User(String username, String firstname, String lastname, String role, String salt, String hashedPassword) {
        this.username = username;
        this.firstname = firstname; 
        this.lastname = lastname; 
        this.role = role;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }
    
    public String getusername() {
        return username;
    }

    public String getfirstname() {
        return firstname;
    }
    
    public String getlastname() {
        return lastname;
    }
    
    public String getrole() {
        return role;
    }
    
    public void setfirstname(String firstname) {
    	this.firstname = firstname ; 
    }
    
    public void setlastname(String lastname) {
    	this.lastname = lastname ; 
    }
	
    public void setrole(String role) {
    	this.role = role ; 
    }
    
    public void setsalt(String salt) {
    	this.salt = salt ; 
    }
    
    public void sethashedPassword(String hashedPassword) {
    	this.hashedPassword = hashedPassword ; 
    }
}

