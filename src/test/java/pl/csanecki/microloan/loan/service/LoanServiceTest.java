package pl.csanecki.microloan.loan.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.csanecki.microloan.loan.dto.UserRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@ExtendWith(SpringExtension.class)
class LoanServiceTest {

    private LoanService loanService;

    @Test
    void shouldAcceptLoanQuery() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-FORWARDED-FOR", "10.0.0.90");
        Instant mockInstant = Instant.now(Clock.fixed(Instant.parse("2018-08-22T10:00:00Z"), ZoneOffset.UTC));

        //when

        //then

        UserRequest userRequest = UserRequest.extractFrom(request);
        System.out.println(userRequest.getRequestTimestamp());
    }


    @Test
    void a()
}