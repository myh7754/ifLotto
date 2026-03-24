package com.example.iflofftob.crawler;

import com.example.iflofftob.crawler.Lt645DrawApiResponse.Item;
import com.example.iflofftob.draw.LotteryDrawRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LotteryDrawCrawlerService 단위 테스트.
 * 실제 RestTemplate을 사용하여 신규 동행복권 API 연결 및 응답 파싱을 검증한다.
 * Repository는 Mock으로 처리하여 DB 없이도 실행 가능하다.
 */

@ExtendWith(MockitoExtension.class)
class LotteryDrawCrawlerServiceTest {

    @Mock
    private LotteryDrawRepository lotteryDrawRepository;

    private LotteryDrawCrawlerService service;

    @BeforeEach
    void setUp() {
        service = new LotteryDrawCrawlerService(new RestTemplate(), lotteryDrawRepository);
    }

    @Test
    @DisplayName("1회차 데이터를 정상적으로 가져온다")
    void fetchDraw_firstDraw_success() {
        Item item = service.fetchDraw(1);

        assertThat(item).isNotNull();
        assertThat(item.getDrawNo()).isEqualTo(1);
        assertThat(item.getDrwtNo1()).isNotNull();
        assertThat(item.getDrawDate()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 회차는 null을 반환한다")
    void fetchDraw_nonExistentDraw_returnsNull() {
        Item item = service.fetchDraw(99999);

        assertThat(item).isNull();
    }

    @Test
    @DisplayName("1000회차 데이터와 당첨 번호 범위를 검증한다")
    void fetchDraw_draw1000_validNumbers() {
        Item item = service.fetchDraw(1000);

        assertThat(item).isNotNull();
        assertThat(item.getDrawNo()).isEqualTo(1000);
        assertThat(item.getDrwtNo1()).isBetween(1, 45);
        assertThat(item.getDrwtNo2()).isBetween(1, 45);
        assertThat(item.getDrwtNo3()).isBetween(1, 45);
        assertThat(item.getDrwtNo4()).isBetween(1, 45);
        assertThat(item.getDrwtNo5()).isBetween(1, 45);
        assertThat(item.getDrwtNo6()).isBetween(1, 45);
        assertThat(item.getBonusNo()).isBetween(1, 45);
        // 날짜 포맷 검증 (yyyyMMdd, 8자리)
        assertThat(item.getDrawDate()).hasSize(8);
    }
}
