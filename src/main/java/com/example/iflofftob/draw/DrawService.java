package com.example.iflofftob.draw;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로또 회차 조회 기능을 제공하는 서비스.
 * 최신 회차 및 특정 회차 정보 반환을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawService {

    private final LotteryDrawRepository drawRepository;

    /** 최신 회차 당첨 정보를 반환한다. 데이터가 없으면 예외를 던진다. */
    public DrawResponse getLatestDraw() {
        LotteryDraw draw = drawRepository.findTopByOrderByDrawNoDesc()
                .orElseThrow(() -> new IllegalStateException("저장된 회차 데이터가 없습니다."));
        return DrawResponse.from(draw);
    }

    /** 특정 회차 번호의 당첨 정보를 반환한다. 없으면 예외를 던진다. */
    public DrawResponse getDraw(int drawNo) {
        LotteryDraw draw = drawRepository.findById(drawNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다: " + drawNo));
        return DrawResponse.from(draw);
    }
}
