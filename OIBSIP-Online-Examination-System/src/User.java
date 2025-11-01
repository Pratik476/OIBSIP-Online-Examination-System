import java.util.*;

public class User {
    private String userId;
    private String name;
    private String password;

    public User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean checkPassword(String p) { return password.equals(p); }
    public void setPassword(String newPass) { this.password = newPass; }
}
