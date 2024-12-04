package org.assignment.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.model.OperationType;
import org.assignment.wallet.model.Wallet;
import org.assignment.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

// e2e tests with flow: request to API -> await expected result in response and in h2.mem database
// H2 mem db will reset after tests
// Use prod RabbitMQ to imitate real user experience

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WalletApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper method instead of @BeforeEach to handle concurrent tests
    private Wallet prepareWallet() {
        var wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(new BigDecimal("1000.00"));
        walletRepository.save(wallet);
        return wallet;
    }

    @Test
    void testGetWalletBalanceSuccess() throws Exception {
        var walletId = prepareWallet().getId();
        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value("1000.00"));

        var wallet = walletRepository.findById(walletId).orElseThrow(RuntimeException::new);
        assertEquals(wallet.getBalance(), new BigDecimal("1000.00"));
    }

    @Test
    void testGetWalletInvalidUUID() throws Exception {
        var walletId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testProcessTransactionSuccess() throws Exception {
        var walletId = prepareWallet().getId();
        var currentBalance = walletRepository.findById(walletId).orElseThrow(RuntimeException::new).getBalance();
        assertEquals(currentBalance, new BigDecimal("1000.00"));

        var request = new TransactionRequestDTO();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        await().atMost(5, SECONDS).untilAsserted(() -> {
            var updatedWallet = walletRepository.findById(walletId).orElseThrow(RuntimeException::new);
            assertEquals(updatedWallet.getBalance(), new BigDecimal("900.00"));
        });

        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("211.11"));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        await().atMost(5, SECONDS).untilAsserted(() -> {
            var updatedWallet = walletRepository.findById(walletId).orElseThrow(RuntimeException::new);
            assertEquals(updatedWallet.getBalance(), new BigDecimal("1111.11"));
        });
    }

    /*
    Test cases from csv:
    - no id
    - no status
    - no amount
    - wrong id
    - wrong status
    - negative amount
    - incorrect amount format - API expect exactly two digits after dot
    */
    @ParameterizedTest
    @CsvFileSource(resources = "/invalidTransactionRequestData.csv", delimiterString = "|", numLinesToSkip = 2)
    void testInvalidTransactionRequests(String requestTemplate, int expectedStatus) throws Exception {
        var walletId = prepareWallet().getId();
        var request = String.format(requestTemplate, walletId);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().is(expectedStatus));
    }

}
