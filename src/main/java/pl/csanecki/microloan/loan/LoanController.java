package pl.csanecki.microloan.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.service.LoanService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoanController {

    private LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/loan/query")
    String queryForLoan(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        UserRequest userRequest = UserRequest.extractFrom(request);

        return "Timestamp: " + userRequest.getRequestTimestamp();
    }
}
