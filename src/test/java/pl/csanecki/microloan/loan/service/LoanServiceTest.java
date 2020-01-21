package pl.csanecki.microloan.loan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.csanecki.microloan.loan.model.Loan;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class LoanServiceTest {

    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImpl();
    }

    @Test
    void shouldAcceptLoanQuery() {
        //given
        Loan expectedLoan = new Loan();

        //when

        //then

    }

}