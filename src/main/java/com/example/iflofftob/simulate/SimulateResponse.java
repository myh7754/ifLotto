package com.example.iflofftob.simulate;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 시뮬레이션 결과를 클라이언트에 반환하는 응답 DTO.
 * 등수별 통계 요약과 전체 회차 상세 결과를 포함한다.
 */
@Getter
@Builder
public class SimulateResponse {

    /** 입력 번호 */
    private List<Integer> inputNumbers;

    /** 조회 대상 총 회차 수 */
    private int totalDrawCount;

    /** 등수별 당첨 횟수 (key: "1등"~"5등", "낙첨") */
    private Map<String, Integer> rankSummary;

    /** 전체 회차별 상세 결과 (최신 회차 기준 내림차순) */
    private List<DrawResult> drawResults;

    /**
     * 회차별 시뮬레이션 결과 항목.
     */
    @Getter
    @Builder
    public static class DrawResult {
        private Integer drawNo;
        private LocalDate drawDate;
        private List<Integer> winningNumbers;
        private Integer bonusNum;
        private int matchCount;      // 일치 번호 개수
        private boolean bonusMatch;  // 보너스 번호 일치 여부
        private String rank;         // "1등"~"5등", "낙첨"
    }
}
