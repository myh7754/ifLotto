package com.example.iflofftob.draw;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 로또 회차별 당첨 정보를 나타내는 JPA 엔티티.
 * 동행복권 API에서 수집한 데이터를 lottery_draw 테이블에 매핑한다.
 */
@Entity
@Table(name = "lottery_draw")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LotteryDraw {

    /** 회차 번호 (Primary Key) */
    @Id
    @Column(name = "draw_no")
    private Integer drawNo;

    /** 추첨일 */
    @Column(name = "draw_date", nullable = false)
    private LocalDate drawDate;

    /** 당첨 번호 1~6 */
    @Column(name = "num1", nullable = false)
    private Integer num1;

    @Column(name = "num2", nullable = false)
    private Integer num2;

    @Column(name = "num3", nullable = false)
    private Integer num3;

    @Column(name = "num4", nullable = false)
    private Integer num4;

    @Column(name = "num5", nullable = false)
    private Integer num5;

    @Column(name = "num6", nullable = false)
    private Integer num6;

    /** 보너스 번호 */
    @Column(name = "bonus_num", nullable = false)
    private Integer bonusNum;

    /** 1등 총 상금액 (원) */
    @Column(name = "first_prize_total", nullable = false)
    private Long firstPrizeTotal;

    /** 1등 1인당 상금액 (원) */
    @Column(name = "first_prize_per_winner", nullable = false)
    private Long firstPrizePerWinner;

    /** 1등 당첨자 수 */
    @Column(name = "first_winner_count", nullable = false)
    private Integer firstWinnerCount;

    /** 데이터 수집 일시 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
