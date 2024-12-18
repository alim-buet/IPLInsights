package dto;

import java.io.Serializable;

public class LogoutRequest implements Serializable {
    private String clubName;

    public LogoutRequest(String clubName) {
        this.clubName = clubName;
    }

    public String getClubName() {
        return clubName;
    }
}
