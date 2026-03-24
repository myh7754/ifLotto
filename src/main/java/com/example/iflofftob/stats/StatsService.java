package com.example.iflofftob.stats;

import com.example.iflofftob.draw.LotteryDraw;
import com.example.iflofftob.draw.LotteryDrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 로또 번호 통계 서비스.
 * 최근 N회차에서 미출현 번호 분석 등 통계 기능을 제공한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final LotteryDrawRepository drawRepository;

    /**
     * 최근 recentCount 회차에서 한 번도 출현하지 않은 번호 목록을 오름차순으로 반환한다.
     * 보너스 번호는 포함하지 않는다.
     */
    public List<Integer> getUnappearedNumbers(int recentCount) {
        if (recentCount < 1) {
            throw new IllegalArgumentException("recentCount는 1 이상이어야 합니다.");
        }

        List<LotteryDraw> recentDraws = drawRepository.findAllByOrderByDrawNoDesc(
                PageRequest.of(0, recentCount)
        );

        // 최근 N회차에 출현한 번호 수집 (보너스 번호 제외)
        Set<Integer> appearedNumbers = new HashSet<>();
        for (LotteryDraw draw : recentDraws) {
            appearedNumbers.add(draw.getNum1());
            appearedNumbers.add(draw.getNum2());
            appearedNumbers.add(draw.getNum3());
            appearedNumbers.add(draw.getNum4());
            appearedNumbers.add(draw.getNum5());
            appearedNumbers.add(draw.getNum6());
        }

        // 1~45 중 미출현 번호 반환
        return IntStream.rangeClosed(1, 45)
                .filter(n -> !appearedNumbers.contains(n))
                .boxed()
                .toList();
    }
}
