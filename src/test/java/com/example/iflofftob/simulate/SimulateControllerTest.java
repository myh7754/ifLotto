package com.example.iflofftob.simulate;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SimulateController 슬라이스 테스트.
 * @Valid 검증 실패 흐름과 정상 응답 구조를 검증한다.
 */
@WebMvcTest(SimulateController.class)
class SimulateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SimulateService simulateService;

    @Test
    @DisplayName("POST /api/simulate 는 유효한 요청에 200을 반환한다")
    void simulate_validRequest_returns200() throws Exception {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        SimulateResponse mockResponse = SimulateResponse.builder()
                .inputNumbers(numbers)
                .totalDrawCount(1)
                .rankSummary(Map.of("1등", 0, "낙첨", 1))
                .drawResults(List.of())
                .build();
        given(simulateService.simulate(numbers)).willReturn(mockResponse);

        mockMvc.perform(post("/api/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("numbers", numbers))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inputNumbers").isArray())
                .andExpect(jsonPath("$.rankSummary").exists())
                .andExpect(jsonPath("$.totalDrawCount").value(1));
    }

    @Test
    @DisplayName("numbers가 null이면 400을 반환한다")
    void simulate_nullNumbers_returns400() throws Exception {
        mockMvc.perform(post("/api/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numbers\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("번호가 5개면 @Size 검증 실패로 400을 반환한다")
    void simulate_fiveNumbers_returns400() throws Exception {
        mockMvc.perform(post("/api/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("numbers", List.of(1, 2, 3, 4, 5)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("번호가 7개면 @Size 검증 실패로 400을 반환한다")
    void simulate_sevenNumbers_returns400() throws Exception {
        mockMvc.perform(post("/api/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("numbers", List.of(1, 2, 3, 4, 5, 6, 7)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("서비스에서 IllegalArgumentException 발생 시 400을 반환한다")
    void simulate_serviceThrowsIllegalArgument_returns400() throws Exception {
        given(simulateService.simulate(anyList()))
                .willThrow(new IllegalArgumentException("번호에 중복이 있습니다."));

        mockMvc.perform(post("/api/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("numbers", List.of(1, 2, 3, 4, 5, 6)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("번호에 중복이 있습니다."));
    }
}
