package com.example.iflofftob.simulate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 시뮬레이션 API 요청 DTO.
 * 6개의 로또 번호를 입력받아 역대 회차 대비 결과를 계산한다.
 */
@Getter
@NoArgsConstructor
public class SimulateRequest {

    @NotNull(message = "번호 목록은 필수입니다.")
    @Size(min = 6, max = 6, message = "번호는 정확히 6개여야 합니다.")
    private List<@NotNull Integer> numbers;
}
