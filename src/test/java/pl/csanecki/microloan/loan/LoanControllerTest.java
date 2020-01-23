package pl.csanecki.microloan.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.LoanStatus;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.service.LoanService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService;

    private MockHttpServletRequest mockRequest;

    private UserRequest mockedUserRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new LoanController(loanService)).build();

        mockRequest = new MockHttpServletRequest();
        mockedUserRequest = UserRequest.extractFrom(mockRequest);

        ReflectionTestUtils.setField(mockedUserRequest, "ip", "10.0.0.90");
        ReflectionTestUtils.setField(mockedUserRequest, "requestTimestamp", LocalDateTime.of(2020, 1, 20, 13, 26));
    }

    @Test
    void shouldGrantLoan() throws Exception {
        //given
        Disposition disposition = new PositiveDisposition("Pożyczka została pomyślnie wydana", LoanStatus.GRANTED, 27L);
        LoanQuery loanQuery = new LoanQuery(BigDecimal.valueOf(10000), 36);

        //when
        when(loanService.considerLoanRequest(mockedUserRequest, loanQuery)).thenReturn(disposition);

        //then
        mockMvc
                .perform(post("/loan/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"amount\": 10000, \"periodInMonths\": 36")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "/loan/27"))
                .andExpect(jsonPath("$.loadId").value(27));
    }
}