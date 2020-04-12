package fudan.se.lab2.controller.request;

/**
 * @author YHT
 */
public class RegisterRequest {
    private String username;

    public RegisterRequest(String username, String password, String fullName, String email, String office, String[] region)
    {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.office = office;
        this.region = region;
    }

    private String password;
    private String fullName;
    private String email;
    private String office;
    private String[] region;

    public RegisterRequest() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOffice() {
        return office;
    }

    public void setRegion(String[] region) {
        this.region = region;
    }

    public String[] getRegion() {
        return region;
    }
}

