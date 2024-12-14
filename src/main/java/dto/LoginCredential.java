package dto;

import java.io.Serializable;

public class LoginCredential implements Serializable {
    private String clubName;
    private String password;

    public LoginCredential(String clubName, String password) {
        this.clubName = clubName;
        this.password = password;
    }

    public String getClubName() {
        return clubName;
    }

    public String getPassword() {
        return password;
    }
}
