package pl.csanecki.microloan.loan.dto;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;

public class UserRequest {
    private String ip;
    private Instant requestTimestamp;

    private UserRequest(HttpServletRequest request) {
        this.ip = resolveIpAddressFrom(request);
        this.requestTimestamp = Clock.systemUTC().instant();
    }

    public static UserRequest extractFrom(HttpServletRequest request) {
        return new UserRequest(request);
    }

    private String resolveIpAddressFrom(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (thereIsNot(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }

        return remoteAddr;
    }

    private boolean thereIsNot(String remoteAddr) {
        return remoteAddr == null || "".equals(remoteAddr);
    }

    public String getIp() {
        return ip;
    }

    public Instant getRequestTimestamp() {
        return requestTimestamp;
    }
}
