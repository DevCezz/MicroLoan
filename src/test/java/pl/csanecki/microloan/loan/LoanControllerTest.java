package pl.csanecki.microloan.loan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.LoanStatus;
import pl.csanecki.microloan.loan.model.PositiveDisposition;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoanController.class)
class LoanControllerTest {

    public static final String DISPOSITION_MESSAGE = "Pożyczka została pomyślnie wydana";
    public static final LoanStatus LOAN_STATUS = LoanStatus.GRANTED;
    public static final long LOAN_ID = 27L;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Test
    void shouldGrantLoan() throws Exception {
        //given
        PositiveDisposition disposition = new PositiveDisposition(DISPOSITION_MESSAGE, LOAN_STATUS, LOAN_ID);

        //when
        when(loanService.considerLoanRequest(any(UserRequest.class), any(LoanQuery.class))).thenReturn(disposition);

        //then
        mockMvc
                .perform(post("/loan/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"amount\":3000, \"periodInMonths\":36 }")
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "/loan/postpone/" + disposition.getLoanId()))
                .andExpect(jsonPath("$.message").value(disposition.getMessage()))
                .andExpect(jsonPath("$.loanStatus").value(disposition.getLoanStatus().toString()))
                .andExpect(jsonPath("$.loanId").value(disposition.getLoanId()));
    }
}