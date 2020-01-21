package pl.csanecki.microloan.loan;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.csanecki.microloan.loan.dto.LoanQuery;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoanController {

    @PostMapping("/loan/query")
    String queryForLoan(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        String ipAddress = resolveIpAddressFrom(request);

        return ipAddress + ": " + loanQuery.toString();
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
}
