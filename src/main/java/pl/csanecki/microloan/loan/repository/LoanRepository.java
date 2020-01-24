package pl.csanecki.microloan.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.csanecki.microloan.loan.model.Loan;
import pl.csanecki.microloan.loan.model.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    int countLoansByClientIpAndStatusIsIn(String clientIp, List<LoanStatus> loanStatuses);
}
