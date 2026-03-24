package com.example.iflofftob.draw;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * lottery_draw 테이블에 대한 데이터 접근 레이어.
 * Spring Data JPA를 통해 기본 CRUD와 커스텀 조회 메서드를 제공한다.
 */
public interface LotteryDrawRepository extends JpaRepository<LotteryDraw, Integer> {

    // DB가 비어있는지 확인 (초기화 감지용)
    boolean existsBy();

    // 최신 회차 번호 조회 (신규 수집 시작점 결정용)
    Optional<LotteryDraw> findTopByOrderByDrawNoDesc();

    // 최근 N회차 조회 (미출현 번호 통계용)
    List<LotteryDraw> findAllByOrderByDrawNoDesc(Pageable pageable);
}
