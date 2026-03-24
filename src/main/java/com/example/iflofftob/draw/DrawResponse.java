package com.example.iflofftob.draw;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 회차 당첨 정보를 클라이언트에 반환하는 응답 DTO.
 * GET /api/draws/latest, GET /api/draws/{drawNo} 공통 사용.
 */
@Getter
@Builder
public class DrawResponse {

    private Integer drawNo;
    private LocalDate drawDate;
    private List<Integer> numbers;
    private Integer bonusNum;
    private Long firstPrizeTotal;
    private Long firstPrizePerWinner;
    private Integer firstWinnerCount;

    public static DrawResponse from(LotteryDraw draw) {
        return DrawResponse.builder()
                .drawNo(draw.getDrawNo())
                .drawDate(draw.getDrawDate())
                .numbers(List.of(
                        draw.getNum1(), draw.getNum2(), draw.getNum3(),
                        draw.getNum4(), draw.getNum5(), draw.getNum6()
                ))
                .bonusNum(draw.getBonusNum())
                .firstPrizeTotal(draw.getFirstPrizeTotal())
                .firstPrizePerWinner(draw.getFirstPrizePerWinner())
                .firstWinnerCount(draw.getFirstWinnerCount())
                .build();
    }
}
