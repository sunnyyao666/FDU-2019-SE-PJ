package fudan.se.lab2.controller.request;

import java.util.Set;

/**
 * @author YHT
 */
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String office;
    private Set<String> authorities;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String email, String office, Set<String> authorities) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.office = office;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}

