package com.example.iflofftob.scheduler;

import com.example.iflofftob.service.LotteryDrawCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 로또 당첨 데이터 자동 수집 스케줄러.
 * 매주 토요일 추첨 후 신규 회차 데이터를 자동으로 수집한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LotteryDrawScheduler {

    private final LotteryDrawCrawlerService crawlerService;

    // 매주 토요일 20:30 실행 (추첨은 20:35)
    @Scheduled(cron = "0 30 20 * * SAT")
    public void collectWeeklyDraw() {
        log.debug("주간 로또 당첨 번호 수집 스케줄러 실행");
        crawlerService.collectLatestDraw();
    }
}
