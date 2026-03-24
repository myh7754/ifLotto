package com.example.iflofftob.stats;

import com.example.iflofftob.draw.LotteryDraw;
import com.example.iflofftob.draw.LotteryDrawRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * StatsService 단위 테스트.
 * 미출현 번호 계산, 보너스 번호 제외 정책, Pageable 전달을 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private LotteryDrawRepository drawRepository;

    @InjectMocks
    private StatsService statsService;

    private static LotteryDraw buildDraw(int drawNo, int n1, int n2, int n3, int n4, int n5, int n6, int bonus) {
        return LotteryDraw.builder()
                .drawNo(drawNo)
                .drawDate(LocalDate.now())
                .num1(n1).num2(n2).num3(n3).num4(n4).num5(n5).num6(n6)
                .bonusNum(bonus)
                .firstPrizeTotal(0L)
                .firstPrizePerWinner(0L)
                .firstWinnerCount(0)
                .build();
    }

    @Test
    @DisplayName("recentCount가 0이면 IllegalArgumentException을 던진다")
    void getUnappearedNumbers_recentCountZero_throws() {
        assertThatThrownBy(() -> statsService.getUnappearedNumbers(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1 이상");
    }

    @Test
    @DisplayName("recentCount가 음수이면 IllegalArgumentException을 던진다")
    void getUnappearedNumbers_negativeCount_throws() {
        assertThatThrownBy(() -> statsService.getUnappearedNumbers(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("출현한 번호는 미출현 목록에 포함되지 않는다")
    void getUnappearedNumbers_someAppeared_returnsUnappeared() {
        given(drawRepository.findAllByOrderByDrawNoDesc(any(Pageable.class))).willReturn(
                List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 45))
        );

        List<Integer> result = statsService.getUnappearedNumbers(1);

        assertThat(result).doesNotContain(1, 2, 3, 4, 5, 6);
        assertThat(result).contains(7, 8, 9, 10, 44, 45);
    }

    @Test
    @DisplayName("미출현 번호 목록은 오름차순으로 정렬된다")
    void getUnappearedNumbers_resultIsSortedAscending() {
        given(drawRepository.findAllByOrderByDrawNoDesc(any(Pageable.class))).willReturn(
                List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 7))
        );

        List<Integer> result = statsService.getUnappearedNumbers(1);

        assertThat(result).isSortedAccordingTo(Integer::compareTo);
    }

    @Test
    @DisplayName("보너스 번호는 출현 번호로 집계하지 않는다")
    void getUnappearedNumbers_bonusNumExcluded() {
        given(drawRepository.findAllByOrderByDrawNoDesc(any(Pageable.class))).willReturn(
                List.of(buildDraw(1, 1, 2, 3, 4, 5, 6, 10))
        );

        List<Integer> result = statsService.getUnappearedNumbers(1);

        // 보너스 번호 10은 미출현 목록에 포함되어야 한다
        assertThat(result).contains(10);
    }

    @Test
    @DisplayName("recentCount 크기의 Pageable을 Repository에 전달한다")
    void getUnappearedNumbers_pageableUsedCorrectly() {
        given(drawRepository.findAllByOrderByDrawNoDesc(any(Pageable.class))).willReturn(List.of());

        statsService.getUnappearedNumbers(5);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(drawRepository).findAllByOrderByDrawNoDesc(captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("회차가 없으면 1~45 전체가 미출현 목록이다")
    void getUnappearedNumbers_noDraws_returnsAllNumbers() {
        given(drawRepository.findAllByOrderByDrawNoDesc(any(Pageable.class))).willReturn(List.of());

        List<Integer> result = statsService.getUnappearedNumbers(1);

        assertThat(result).hasSize(45);
    }
}
