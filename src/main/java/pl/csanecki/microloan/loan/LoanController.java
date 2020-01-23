package pl.csanecki.microloan.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.service.LoanService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
public class LoanController {

    private LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping(
        value = "/loan/query",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Disposition> queryForLoan(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        UserRequest userRequest = UserRequest.extractFrom(request);
        Disposition disposition = loanService.considerLoanRequest(userRequest, loanQuery);

        return responseConsidering(disposition);
    }

    private ResponseEntity<Disposition> responseConsidering(Disposition disposition) {
        if(isPositiveDisposition(disposition)) {
            return ResponseEntity
                    .created(resolveLocationUri((PositiveDisposition) disposition))
                    .body(disposition);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(disposition);
        }
    }

    private URI resolveLocationUri(PositiveDisposition disposition) {
        return UriComponentsBuilder
                .fromUriString("/loan/postpone/{id}")
                .buildAndExpand(disposition.getLoanId())
                .toUri();
    }

    private boolean isPositiveDisposition(Disposition disposition) {
        return disposition instanceof PositiveDisposition;
    }
}
