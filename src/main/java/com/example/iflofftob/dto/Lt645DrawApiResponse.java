package com.example.iflofftob.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 동행복권 신규 로또 결과 API 응답 JSON을 매핑하는 DTO.
 * API URL: https://www.dhlottery.co.kr/lt645/selectPstLt645InfoNew.do?srchDir=center&srchLtEpsd={drwNo}
 *
 * 응답 예시:
 * {
 *   "resultCode": null,
 *   "data": {
 *     "list": [
 *       { "ltEpsd": 1004, "tm1WnNo": 7, ..., "bnsWnNo": 18, "ltRflYmd": "20220226", ... }
 *     ]
 *   }
 * }
 */
@Getter
@NoArgsConstructor
public class Lt645DrawApiResponse {

    @JsonProperty("data")
    private DataWrapper data;

    public boolean hasData() {
        return data != null && data.getList() != null && !data.getList().isEmpty();
    }

    /** 리스트에서 특정 회차 번호와 일치하는 항목을 반환한다. 없으면 null. */
    public Item getItemByDrawNo(int drawNo) {
        if (!hasData()) return null;
        return data.getList().stream()
                .filter(item -> Integer.valueOf(drawNo).equals(item.getDrawNo()))
                .findFirst()
                .orElse(null);
    }

    @Getter
    @NoArgsConstructor
    public static class DataWrapper {
        @JsonProperty("list")
        private List<Item> list;
    }

    /** 개별 회차 데이터 */
    @Getter
    @NoArgsConstructor
    public static class Item {

        /** 회차 번호 */
        @JsonProperty("ltEpsd")
        private Integer drawNo;

        /** 당첨 번호 1~6 */
        @JsonProperty("tm1WnNo")
        private Integer drwtNo1;

        @JsonProperty("tm2WnNo")
        private Integer drwtNo2;

        @JsonProperty("tm3WnNo")
        private Integer drwtNo3;

        @JsonProperty("tm4WnNo")
        private Integer drwtNo4;

        @JsonProperty("tm5WnNo")
        private Integer drwtNo5;

        @JsonProperty("tm6WnNo")
        private Integer drwtNo6;

        /** 보너스 번호 */
        @JsonProperty("bnsWnNo")
        private Integer bonusNo;

        /** 추첨일 (yyyyMMdd 형식 문자열, 예: "20220226") */
        @JsonProperty("ltRflYmd")
        private String drawDate;

        /** 1등 총 상금액 (원) */
        @JsonProperty("rnk1SumWnAmt")
        private Long firstAccumamnt;

        /** 1등 1인당 상금액 (원) */
        @JsonProperty("rnk1WnAmt")
        private Long firstWinamnt;

        /** 1등 당첨자 수 */
        @JsonProperty("rnk1WnNope")
        private Integer firstPrzwnerCo;
    }
}
