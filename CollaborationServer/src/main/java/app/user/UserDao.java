package app.user;

import java.util.List;
import java.util.stream.Collectors;

//import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static app.Main.userDao;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.google.common.collect.ImmutableList;

public class UserDao {

    private final String userFile = "src/main/resources/data/users.json";
	
    private List<User> users = ImmutableList.of(
        //        Username    Salt for hash                    Hashed password (the password is "password" for all users)
//        new User("Admin", "admin", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEMLT83E1KDmUhCy"),
//    	new User("Mohammadreza", "designer", "$2a$10$h.dl5J86rGH7I8bD9bZeZe", "$2a$10$h.dl5J86rGH7I8bD9bZeZeci0pDt0.VwFTGujlnEaZXPf/q7vM5wO"),
//        new User("Bahman", "designer", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEMLT83E1KDmUhCy"),
//        new User("Gerson", "designer", "$2a$10$E3DgchtVry3qlYlzJCsyxe", "$2a$10$E3DgchtVry3qlYlzJCsyxeSK0fftK4v0ynetVCuDdxGVl1obL.ln2")
    );

    public User getUserByUsername(String username) {
        return users.stream().filter(b -> b.username.equals(username)).findFirst().orElse(null);
    }

    public String getUserRoleByUsername(String username) {
        User u = users.stream().filter(b -> b.username.equals(username)).findFirst().orElse(null);
        if(u!=null)
        	return u.getrole();
        else
        	return null ; 
    }
    
    public Iterable<String> getAllUserNames() {
        return users.stream().map(user -> user.username).collect(Collectors.toList());
    }

    
    public Iterable<User> getAllUsers() {
        return users;
    }

    public void addUser(User user) throws IOException {
    	users = ImmutableList.<User>builder()
                .addAll(users)
                .add(user)
                .build();
    	
    	writeUsers();
    }
    
    public void updateUserbyAdmin(String username, String firstname, String lastname, String role/*, String oldPassword, String newPassword*/) throws IOException {
        User u = userDao.getUserByUsername(username);
		if(u!=null) {
/*	        String hashedPassword = BCrypt.hashpw(oldPassword, u.salt);
	        if (hashedPassword.equals(u.hashedPassword)) {	
                String newSalt = BCrypt.gensalt();
                String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
*/
                u.setfirstname(firstname);
                u.setlastname(lastname);
                u.setrole(role);
/*                u.setsalt(newSalt);
                u.sethashedPassword(newHashedPassword);
    		}*/
		}
    	writeUsers();
    }
    
    public void writeUsers() throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(userFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
            
            Gson gson = new Gson();
            gson.toJson(this.users, isr);
        }
        
    }
    
    
    public void readUsers() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(userFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
        	User[] userList = gson.fromJson(reader, User[].class);
            
        	if(userList!=null) {
	            Arrays.stream(userList).forEach( u -> {

	                try {
						this.addUser(u) ;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            });
        	}
        }
        
    }
    
}
