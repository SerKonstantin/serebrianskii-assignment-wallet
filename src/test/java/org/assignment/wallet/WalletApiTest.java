//package org.assignment.wallet;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.assignment.wallet.dto.TransactionRequestDTO;
//import org.assignment.wallet.model.OperationType;
//import org.assignment.wallet.model.Wallet;
//import org.assignment.wallet.repository.WalletRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class WalletApiTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WalletRepository walletRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private UUID walletId;
//
//    @BeforeEach
//    void setup() {
//        walletId = UUID.randomUUID();
//        var wallet = new Wallet();
//        wallet.setId(walletId);
//        var testValue = new BigDecimal("1000.00");
//        wallet.setBalance(testValue);
//        walletRepository.save(wallet);
//    }
//
//    @Test
//    void testGetWalletBalance() throws Exception {
//        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId.toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
//                .andExpect(jsonPath("$.balance").value("1000.00"));
//
//        var wallet = walletRepository.findById(walletId).orElseThrow(RuntimeException::new);
//        assertEquals(wallet.getBalance(), new BigDecimal("1000.00"));
//    }
//
//    @Test
//    void testProcessTransaction() throws Exception {
//        var currentBalance = walletRepository.findById(walletId).orElseThrow(RuntimeException::new).getBalance();
//        assertEquals(currentBalance, new BigDecimal("1000.00"));
//
//        var request = new TransactionRequestDTO();
//        request.setWalletId(walletId);
//        request.setOperationType(OperationType.WITHDRAW);
//        request.setAmount(new BigDecimal("100.00"));
//        mockMvc.perform(post("/api/v1/wallets")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        currentBalance = walletRepository.findById(walletId).orElseThrow(RuntimeException::new).getBalance();
//        assertEquals(currentBalance, new BigDecimal("900.00"));
//
//        request.setOperationType(OperationType.DEPOSIT);
//        request.setAmount(new BigDecimal("211.11"));
//        mockMvc.perform(post("/api/v1/wallets")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        currentBalance = walletRepository.findById(walletId).orElseThrow(RuntimeException::new).getBalance();
//        assertEquals(currentBalance, new BigDecimal("1111.11"));
//    }
//
//
//}
