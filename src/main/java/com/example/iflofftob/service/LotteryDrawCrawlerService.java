package com.example.iflofftob.service;

import com.example.iflofftob.domain.LotteryDraw;
import com.example.iflofftob.dto.Lt645DrawApiResponse;
import com.example.iflofftob.repository.LotteryDrawRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 동행복권 신규 API에서 로또 당첨 데이터를 수집하는 크롤러 서비스.
 *
 * 주요 책임:
 * 1. 애플리케이션 시작 시 DB가 비어있으면 전체 역대 회차 데이터를 일괄 수집
 * 2. 단일 회차 API 호출 (최대 3회 재시도)
 * 3. 스케줄러에 의해 호출되는 최신 회차 수집
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryDrawCrawlerService {

    /** 동행복권 신규 로또 회차 조회 API URL 템플릿 */
    private static final String API_URL =
            "https://www.dhlottery.co.kr/lt645/selectPstLt645InfoNew.do?srchDir=center&srchLtEpsd={drwNo}";

    /** 추첨일 포맷: API가 "20220226" 형식으로 반환 */
    private static final DateTimeFormatter DRAW_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    /** API 호출 실패 시 최대 재시도 횟수 */
    private static final int MAX_RETRY = 3;

    private final RestTemplate restTemplate;
    private final LotteryDrawRepository lotteryDrawRepository;

    /**
     * 애플리케이션 시작 직후 실행되는 초기화 메서드.
     * DB에 데이터가 없으면 1회차부터 최신 회차까지 전체 데이터를 수집한다.
     * 이미 데이터가 있으면 아무 작업도 수행하지 않는다.
     */
    @PostConstruct
    public void initializeIfEmpty() {
        if (lotteryDrawRepository.existsBy()) {
            log.debug("lottery_draw 테이블에 데이터가 존재합니다. 초기 수집을 건너뜁니다.");
            return;
        }

        log.info("lottery_draw 테이블이 비어있습니다. 전체 역대 회차 수집을 시작합니다.");
        int drawNo = 1;

        // 해당 회차 항목을 찾을 수 없을 때까지 순서대로 수집
        while (true) {
            Lt645DrawApiResponse.Item item = fetchDraw(drawNo);

            if (item == null) {
                log.info("전체 수집 완료. 총 {} 회차를 수집했습니다.", drawNo - 1);
                break;
            }

            saveDraw(item);
            log.debug("{}회차 저장 완료", drawNo);
            drawNo++;
        }
    }

    /**
     * DB에 저장된 최신 회차 이후의 신규 회차를 수집한다.
     * 스케줄러 또는 수동 호출 시 사용된다.
     */
    public void collectLatestDraw() {
        int nextDrawNo = lotteryDrawRepository.findTopByOrderByDrawNoDesc()
                .map(draw -> draw.getDrawNo() + 1)
                .orElse(1);

        log.debug("신규 회차 수집 시작. 시작 회차: {}", nextDrawNo);

        int collected = 0;
        int drawNo = nextDrawNo;

        while (true) {
            Lt645DrawApiResponse.Item item = fetchDraw(drawNo);

            if (item == null) {
                break;
            }

            saveDraw(item);
            collected++;
            drawNo++;
        }

        if (collected > 0) {
            log.info("신규 회차 {}건 수집 완료. ({}회차 ~ {}회차)", collected, nextDrawNo, drawNo - 1);
        } else {
            log.debug("수집할 신규 회차가 없습니다.");
        }
    }

    /**
     * 동행복권 API에서 특정 회차 데이터를 조회한다.
     * API는 요청 회차 근처 10개를 배치로 반환하므로, 정확한 회차를 필터링한다.
     * 실패 시 최대 MAX_RETRY(3)회까지 재시도하며, 재시도 사이에 점진적 대기를 수행한다.
     *
     * @param drawNo 조회할 회차 번호
     * @return 해당 회차 데이터. 존재하지 않거나 최대 재시도 초과 시 null 반환
     */
    public Lt645DrawApiResponse.Item fetchDraw(int drawNo) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                Lt645DrawApiResponse response = restTemplate.getForObject(API_URL, Lt645DrawApiResponse.class, drawNo);
                // API는 배치로 반환하므로 정확한 회차를 찾아 반환
                return response != null ? response.getItemByDrawNo(drawNo) : null;
            } catch (Exception e) {
                log.warn("{}회차 API 호출 실패 (시도 {}/{}): {}", drawNo, attempt, MAX_RETRY, e.getMessage());

                if (attempt == MAX_RETRY) {
                    log.error("{}회차 API 호출 최종 실패. 최대 재시도 횟수({})를 초과했습니다.", drawNo, MAX_RETRY);
                } else {
                    try {
                        Thread.sleep(1000L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private void saveDraw(Lt645DrawApiResponse.Item item) {
        if (lotteryDrawRepository.existsById(item.getDrawNo())) {
            log.debug("{}회차는 이미 저장되어 있습니다.", item.getDrawNo());
            return;
        }

        LotteryDraw draw = LotteryDraw.builder()
                .drawNo(item.getDrawNo())
                .drawDate(LocalDate.parse(item.getDrawDate(), DRAW_DATE_FORMAT))
                .num1(item.getDrwtNo1())
                .num2(item.getDrwtNo2())
                .num3(item.getDrwtNo3())
                .num4(item.getDrwtNo4())
                .num5(item.getDrwtNo5())
                .num6(item.getDrwtNo6())
                .bonusNum(item.getBonusNo())
                .firstPrizeTotal(item.getFirstAccumamnt())
                .firstPrizePerWinner(item.getFirstWinamnt())
                .firstWinnerCount(item.getFirstPrzwnerCo())
                .build();

        lotteryDrawRepository.save(draw);
    }
}
