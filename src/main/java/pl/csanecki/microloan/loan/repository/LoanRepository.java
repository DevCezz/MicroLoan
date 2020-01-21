package pl.csanecki.microloan.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.csanecki.microloan.loan.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
