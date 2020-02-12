package pl.csanecki.microloan.loan.dto.factory;

import pl.csanecki.microloan.loan.dto.UserRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class UserRequestFactory {
    private UserRequestFactory() {}

    public static UserRequest create(HttpServletRequest request) {
        String ip = resolveIpAddressFrom(request);
        LocalDateTime timestamp = LocalDateTime.now();

        return new UserRequest(ip, timestamp.toString());
    }

    private static String resolveIpAddressFrom(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (thereIsNot(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }

        return remoteAddr;
    }

    private static boolean thereIsNot(String remoteAddr) {
        return remoteAddr == null || "".equals(remoteAddr);
    }
}
