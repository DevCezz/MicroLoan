package pl.csanecki.microloan.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.dto.factory.UserRequestFactory;
import pl.csanecki.microloan.loan.model.disposition.Disposition;
import pl.csanecki.microloan.loan.model.disposition.PositiveDisposition;
import pl.csanecki.microloan.loan.model.postponement.PositivePostponement;
import pl.csanecki.microloan.loan.model.postponement.PostponementDecision;

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
            value = "/loan/postpone/{loanId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<PostponementDecision> queryForPostponeLoan(@PathVariable Long loanId, HttpServletRequest request) {
        UserRequest userRequest = UserRequestFactory.create(request);
        PostponementDecision postponementDecision = loanService.postponeLoan(userRequest, loanId);

        return responseConsidering(postponementDecision);
    }

    private ResponseEntity<PostponementDecision> responseConsidering(PostponementDecision postponementDecision) {
        if(isPositivePostponement(postponementDecision)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(postponementDecision);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body(postponementDecision);
        }
    }

    private boolean isPositivePostponement(PostponementDecision postponementDecision) {
        return postponementDecision instanceof PositivePostponement;
    }

    @PostMapping(
        value = "/loan/query",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Disposition> queryForLoan(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        UserRequest userRequest = UserRequestFactory.create(request);
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

    private boolean isPositiveDisposition(Disposition disposition) {
        return disposition instanceof PositiveDisposition;
    }

    private URI resolveLocationUri(PositiveDisposition disposition) {
        return UriComponentsBuilder
                .fromUriString("/loan/postpone/{id}")
                .buildAndExpand(disposition.getLoanId())
                .toUri();
    }
}
