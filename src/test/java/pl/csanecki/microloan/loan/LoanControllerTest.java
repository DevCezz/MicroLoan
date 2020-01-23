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
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.LoanStatus;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.service.LoanService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Test
    void shouldGrantLoan() throws Exception {
        //given
        Disposition disposition = new PositiveDisposition("Pożyczka została pomyślnie wydana", LoanStatus.GRANTED, 27L);

        //when
        when(loanService.considerLoanRequest(any(UserRequest.class), any(LoanQuery.class))).thenReturn(disposition);

        //then
        mockMvc
                .perform(post("/loan/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"amount\":10000, \"periodInMonths\":36 }")
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{ \"message\":\"Pożyczka została pomyślnie wydana\", \"loanStatus\":\"GRANTED\", \"loanId\":27 }"))
                .andExpect(header().string("Location", "/loan/postpone/27"))
                .andExpect(jsonPath("$.message").value("Pożyczka została pomyślnie wydana"))
                .andExpect(jsonPath("$.loanStatus").value("GRANTED"))
                .andExpect(jsonPath("$.loanId").value(27));
    }
}