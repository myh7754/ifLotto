package com.example.iflofftob.draw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * DrawService 단위 테스트.
 * Repository를 Mock으로 처리하여 서비스 로직만 검증한다.
 */
@ExtendWith(MockitoExtension.class)
class DrawServiceTest {

    @Mock
    private LotteryDrawRepository drawRepository;

    @InjectMocks
    private DrawService drawService;

    private static LotteryDraw buildDraw(int drawNo) {
        return LotteryDraw.builder()
                .drawNo(drawNo)
                .drawDate(LocalDate.of(2024, 1, 6))
                .num1(1).num2(2).num3(3).num4(4).num5(5).num6(6)
                .bonusNum(7)
                .firstPrizeTotal(3_000_000_000L)
                .firstPrizePerWinner(1_500_000_000L)
                .firstWinnerCount(2)
                .build();
    }

    @Test
    @DisplayName("최신 회차가 존재하면 DrawResponse를 반환한다")
    void getLatestDraw_success() {
        given(drawRepository.findTopByOrderByDrawNoDesc()).willReturn(Optional.of(buildDraw(1108)));

        DrawResponse response = drawService.getLatestDraw();

        assertThat(response.getDrawNo()).isEqualTo(1108);
        assertThat(response.getNumbers()).hasSize(6).containsExactly(1, 2, 3, 4, 5, 6);
        assertThat(response.getBonusNum()).isEqualTo(7);
    }

    @Test
    @DisplayName("저장된 데이터가 없으면 IllegalStateException을 던진다")
    void getLatestDraw_emptyRepository_throwsIllegalState() {
        given(drawRepository.findTopByOrderByDrawNoDesc()).willReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.getLatestDraw())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("저장된 회차 데이터가 없습니다");
    }

    @Test
    @DisplayName("존재하는 회차 번호로 조회하면 DrawResponse를 반환한다")
    void getDraw_existingDrawNo_success() {
        given(drawRepository.findById(1108)).willReturn(Optional.of(buildDraw(1108)));

        DrawResponse response = drawService.getDraw(1108);

        assertThat(response.getDrawNo()).isEqualTo(1108);
        assertThat(response.getNumbers()).hasSize(6);
    }

    @Test
    @DisplayName("존재하지 않는 회차 번호는 IllegalArgumentException을 던진다")
    void getDraw_nonExistentDrawNo_throwsIllegalArgument() {
        given(drawRepository.findById(9999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.getDraw(9999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("9999");
    }
}
