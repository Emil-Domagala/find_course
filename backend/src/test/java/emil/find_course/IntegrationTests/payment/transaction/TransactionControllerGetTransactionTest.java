package emil.find_course.IntegrationTests.payment.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.course.courseStudent.PrepareCourseWithStudentUtil;
import emil.find_course.IntegrationTests.payment.PrepareTransactionUtil;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.payment.transaction.dto.TransactionDto;
import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TransactionControllerGetTransactionTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrepareCourseWithStudentUtil prepareCourseWithStudentUtil;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private PrepareTransactionUtil prepareTransactionUtil;

    @Autowired
    private EntityManager entityManager;

    User user;
    String token;

    @BeforeEach
    public void setup() {
        user = prepareUserUtil.prepareVerifiedUser();
        token = jwtUtils.generateToken(user);
    }

    private PagingResult<TransactionDto> extracResult(MvcResult res) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PagingResult.class, TransactionDto.class);
        return objectMapper.readValue(res.getResponse().getContentAsString(), type);
    }

    @Test
    public void transactionController_getTransaction_returnsTransaction() throws Exception {
        var c1 = prepareCourseWithStudentUtil.prepareCourseWithChapters(user, 2);
        prepareTransactionUtil.prepareTransaction(user, Set.of(c1));

        entityManager.flush();
        entityManager.clear();

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/transaction").cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extracResult(res);
        assertThat(result.getPage()).isEqualTo(PaginationRequest.DEFAULT_PAGE + 1);
        assertThat(result.getSize()).isEqualTo(PaginationRequest.DEFAULT_SIZE);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getContent()).hasSize(1);
        var t = result.getContent().stream().findFirst().get();
        assertThat(t.getAmount()).isEqualTo(c1.getPrice());
    }

    // returns multiple transactions
    @Test
    public void transactionController_getTransaction_returnsMultipleTransactions() throws Exception {
        List<Transaction> tA = prepareTransactionUtil.prepareUniqueCoursesAndTransactions(user, 2);
        var c1 = tA.get(0).getCourses().stream().toList().get(0);
        var c2 = tA.get(1).getCourses().stream().toList().get(0);

        entityManager.flush();
        entityManager.clear();

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/transaction").cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extracResult(res);
        assertThat(result.getPage()).isEqualTo(PaginationRequest.DEFAULT_PAGE + 1);
        assertThat(result.getSize()).isEqualTo(PaginationRequest.DEFAULT_SIZE);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getContent()).hasSize(2);
        var t1 = result.getContent().stream().findFirst().get();
        var t2 = result.getContent().stream().toList().get(1);
        assertThat(t1.getAmount()).isEqualTo(c1.getPrice());
        assertThat(t2.getAmount()).isEqualTo(c2.getPrice());
    }

    // return sucessfully transaction with multiple courses
    @Test
    public void transactionController_getTransaction_returnsTransactionWithMultipleCourses() throws Exception {
        var c1 = prepareCourseWithStudentUtil.prepareCourse(user);
        var c2 = prepareCourseWithStudentUtil.prepareCourse(user);
        prepareTransactionUtil.prepareTransaction(user, Set.of(c1, c2));

        entityManager.flush();
        entityManager.clear();

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/transaction").cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extracResult(res);
        assertThat(result.getPage()).isEqualTo(PaginationRequest.DEFAULT_PAGE + 1);
        assertThat(result.getSize()).isEqualTo(PaginationRequest.DEFAULT_SIZE);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getContent()).hasSize(1);
        var t1 = result.getContent().stream().findFirst().get();

        assertThat(t1.getAmount()).isEqualTo(c1.getPrice() + c2.getPrice());
    }

    @Test
    public void transactionController_getTransaction_returnsMax100Transactions() throws Exception {
        int size = 102;
        prepareTransactionUtil.prepareUniqueCoursesAndTransactions(user, size);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction").param("size", String.valueOf(size))
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages")
                        .value((int) Math.ceil(size / 100) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(100));

    }

    @Test
    public void transactionController_getTransaction_shouldReturn200ForInvalidInputsThatGetsToDefault()
            throws Exception {
        prepareTransactionUtil.prepareUniqueCoursesAndTransactions(user, 4);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction")
                .param("page", "-1")
                .param("size", "-5")
                .param("sortField", "unknownField")
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(
                        PaginationRequest.DEFAULT_PAGE + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(
                        PaginationRequest.DEFAULT_SIZE))
                .andReturn();

        var content = extracResult(res).getContent().stream().toList();

        for (int i = 0; i < content.size() - 1; i++) {
            Instant current = content.get(i).getCreatedAt();
            Instant next = content.get(i + 1).getCreatedAt();
            assertTrue(current.isBefore(next));
        }

    }

    @ParameterizedTest
    @CsvSource({ "ASC", "DESC" })
    public void transactionController_getTransaction_sortDirectionWorks(String direction) throws Exception {
        int totalElem = 20;
        prepareTransactionUtil.prepareUniqueCoursesAndTransactions(user, totalElem);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction")
                .param("size", Integer.toString(totalElem))
                .param("sortField", "id")
                .param("direction", direction)
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(totalElem))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(totalElem))
                .andReturn();

        var content = extracResult(res).getContent().stream().toList();

        for (int i = 0; i < content.size() - 1; i++) {
            String currentIdStr = content.get(i).getId().toString();
            String nextIdStr = content.get(i + 1).getId().toString();

            if ("ASC".equals(direction)) {
                assertThat(currentIdStr).isLessThan(nextIdStr);
            } else {
                assertThat(currentIdStr).isGreaterThan(nextIdStr);
            }
        }
    }

    @ParameterizedTest(name = "Pagination works=> size {0}, total elements {1}, current page {2}")
    @CsvSource({
            "5,10,0",
            "10,10,0",
            "5,10,1",
    })
    public void transactionController_getTransaction_paginationWorks(String size, String totEle, String currPage)
            throws Exception {
        prepareTransactionUtil.prepareUniqueCoursesAndTransactions(user, Integer.parseInt(totEle));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction")
                .param("page", currPage)
                .param("size", size)
                .cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(totEle))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page")
                        .value(Integer.parseInt(currPage) + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(size))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))
                .andReturn();

    }

    @Test
    public void transactionController_getTransaction_returnsEmptyTransaction() throws Exception {

        var res = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/transaction").cookie(new Cookie(authCookieName, token)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        var result = extracResult(res);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
    }

}
