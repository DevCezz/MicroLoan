package pl.csanecki.microloan.loan.dto;

public class UserRequest {
    public final String ip;
    public final String requestTimestamp;

    public UserRequest(String ip, String requestTimestamp) {
        this.ip = ip;
        this.requestTimestamp = requestTimestamp;
    }
}
