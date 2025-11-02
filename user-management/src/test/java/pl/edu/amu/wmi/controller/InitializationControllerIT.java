package pl.edu.amu.wmi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.amu.wmi.model.user.CoordinatorDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestConfig.class)
class InitializationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUsersCount() throws Exception {

        // when
        MvcResult result = mockMvc.perform(get("/user/initialization/count"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String body = result.getResponse().getContentAsString();
        assertThat(body).isEqualTo("9");
    }

    @Test
    void initializeCoordinator_returnsConflict() throws Exception {
        // given
        CoordinatorDTO dto = new CoordinatorDTO();
        dto.setName("Should Fail");
        dto.setEmail("fail@uni.pl");

        // when & then
        mockMvc.perform(
                        post("/user/initialization/coordinator")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }
}
