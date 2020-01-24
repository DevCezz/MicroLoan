package pl.csanecki.microloan.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.csanecki.microloan.loan.model.Loan;
import pl.csanecki.microloan.loan.model.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    int countLoansByClientIpAndStatus(String clientIp, LoanStatus loanStatus);
    int countLoansByClientIpAndStatusNot(String clientIp, LoanStatus loanStatus);
}
