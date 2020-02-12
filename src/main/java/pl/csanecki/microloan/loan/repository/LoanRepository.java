package pl.csanecki.microloan.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.csanecki.microloan.loan.model.db.Loan;
import pl.csanecki.microloan.loan.model.db.LoanStatus;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    int countLoansByClientIpAndStatusIsIn(String clientIp, List<LoanStatus> loanStatuses);
}
