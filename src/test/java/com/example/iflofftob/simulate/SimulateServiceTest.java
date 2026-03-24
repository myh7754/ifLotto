package com.example.iflofftob.simulate;

import com.example.iflofftob.draw.LotteryDraw;
import com.example.iflofftob.draw.LotteryDrawRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * SimulateService 단위 테스트.
 * validate()의 입력값 검증과 simulate()의 등수 결정 로직을 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class SimulateServiceTest {

    @Mock
    private LotteryDrawRepository drawRepository;

    @InjectMocks
    private SimulateService simulateService;

    private static LotteryDraw buildDraw(int drawNo, int n1, int n2, int n3, int n4, int n5, int n6, int bonus) {
        return LotteryDraw.builder()
                .drawNo(drawNo)
                .drawDate(LocalDate.of(2024, 1, 6))
                .num1(n1).num2(n2).num3(n3).num4(n4).num5(n5).num6(n6)
                .bonusNum(bonus)
                .firstPrizeTotal(0L)
                .firstPrizePerWinner(0L)
                .firstWinnerCount(0)
                .build();
    }

    // ── validate() ──────────────────────────────────────

    @Test
    @DisplayName("유효한 6개 번호는 예외 없이 통과한다")
    void validate_validNumbers_noException() {
        assertThatNoException()
                .isThrownBy(() -> simulateService.validate(List.of(1, 2, 3, 4, 5, 6)));
    }

    @Test
    @DisplayName("경계값 1과 45는 유효하다")
    void validate_boundaryNumbers_noException() {
        assertThatNoException()
                .isThrownBy(() -> simulateService.validate(List.of(1, 2, 3, 4, 5, 45)));
    }

    @Test
    @DisplayName("중복 번호가 있으면 IllegalArgumentException을 던진다")
    void validate_duplicateNumbers_throws() {
        assertThatThrownBy(() -> simulateService.validate(List.of(1, 1, 2, 3, 4, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("중복");
    }

    @Test
    @DisplayName("1 미만 번호는 IllegalArgumentException을 던진다")
    void validate_numberBelowRange_throws() {
        assertThatThrownBy(() -> simulateService.validate(List.of(0, 2, 3, 4, 5, 6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1~45");
    }

    @Test
    @DisplayName("45 초과 번호는 IllegalArgumentException을 던진다")
    void validate_numberAboveRange_throws() {
        assertThatThrownBy(() -> simulateService.validate(List.of(46, 2, 3, 4, 5, 6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1~45");
    }

    // ── simulate() 등수 결정 ────────────────────────────

    @Test
    @DisplayName("6개 일치 시 1등이다")
    void simulate_allSixMatch_rank1() {
        given(drawRepository.findAll()).willReturn(
                new ArrayList<>(List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7)))
        );

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 4, 5, 6));

        assertThat(response.getRankSummary().get("1등")).isEqualTo(1);
    }

    @Test
    @DisplayName("5개 일치 + 보너스 일치 시 2등이다")
    void simulate_fiveMatchWithBonus_rank2() {
        given(drawRepository.findAll()).willReturn(
                new ArrayList<>(List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7)))
        );

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 4, 5, 7));

        assertThat(response.getRankSummary().get("2등")).isEqualTo(1);
    }

    @Test
    @DisplayName("5개 일치 + 보너스 불일치 시 3등이다")
    void simulate_fiveMatchWithoutBonus_rank3() {
        given(drawRepository.findAll()).willReturn(
                new ArrayList<>(List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7)))
        );

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 4, 5, 8));

        assertThat(response.getRankSummary().get("3등")).isEqualTo(1);
    }

    @Test
    @DisplayName("4개 일치 시 4등이다")
    void simulate_fourMatch_rank4() {
        given(drawRepository.findAll()).willReturn(
                new ArrayList<>(List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7)))
        );

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 4, 40, 41));

        assertThat(response.getRankSummary().get("4등")).isEqualTo(1);
    }

    @Test
    @DisplayName("3개 일치 시 5등이다")
    void simulate_threeMatch_rank5() {
        given(drawRepository.findAll()).willReturn(
                new ArrayList<>(List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7)))
        );

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 40, 41, 42));

        assertThat(response.getRankSummary().get("5등")).isEqualTo(1);
    }

    @Test
    @DisplayName("2개 이하 일치 시 낙첨이다")
    void simulate_twoOrLessMatch_noPrize() {
        given(drawRepository.findAll()).willReturn(
                new ArrayList<>(List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7)))
        );

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 40, 41, 42, 43));

        assertThat(response.getRankSummary().get("낙첨")).isEqualTo(1);
    }

    @Test
    @DisplayName("totalDrawCount는 전체 회차 수와 같다")
    void simulate_totalDrawCountMatchesRepositorySize() {
        given(drawRepository.findAll()).willReturn(new ArrayList<>(List.of(
                buildDraw(1, 1, 2, 3, 4, 5, 6, 7),
                buildDraw(2, 10, 20, 30, 40, 41, 42, 43)
        )));

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 4, 5, 6));

        assertThat(response.getTotalDrawCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("결과는 최신 회차 내림차순으로 정렬된다")
    void simulate_resultIsSortedByDrawNoDesc() {
        given(drawRepository.findAll()).willReturn(new ArrayList<>(List.of(
                buildDraw(1, 10, 11, 12, 13, 14, 15, 16),
                buildDraw(5, 1, 2, 3, 4, 5, 6, 7)
        )));

        SimulateResponse response = simulateService.simulate(List.of(1, 2, 3, 4, 5, 6));

        assertThat(response.getDrawResults().get(0).getDrawNo()).isEqualTo(5);
        assertThat(response.getDrawResults().get(1).getDrawNo()).isEqualTo(1);
    }
}
