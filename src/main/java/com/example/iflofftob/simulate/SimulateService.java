package com.example.iflofftob.simulate;

import com.example.iflofftob.draw.LotteryDraw;
import com.example.iflofftob.draw.LotteryDrawRepository;
import com.example.iflofftob.simulate.SimulateResponse.DrawResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 로또 번호 시뮬레이션 서비스.
 * 입력된 6개 번호를 역대 모든 회차와 비교하여 등수 통계와 상세 결과를 반환한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimulateService {

    private final LotteryDrawRepository drawRepository;

    /**
     * 입력 번호가 1~45 범위인지, 중복이 없는지 검증한다.
     * Spring Validation 이후의 2차 비즈니스 검증.
     */
    public void validate(List<Integer> numbers) {
        Set<Integer> unique = new HashSet<>(numbers);
        if (unique.size() != 6) {
            throw new IllegalArgumentException("번호에 중복이 있습니다.");
        }
        for (int n : numbers) {
            if (n < 1 || n > 45) {
                throw new IllegalArgumentException("번호는 1~45 범위여야 합니다: " + n);
            }
        }
    }

    /** 전체 역대 회차를 대상으로 시뮬레이션을 실행하고 결과를 반환한다. */
    public SimulateResponse simulate(List<Integer> inputNumbers) {
        validate(inputNumbers);

        Set<Integer> inputSet = new HashSet<>(inputNumbers);
        List<LotteryDraw> allDraws = drawRepository.findAll();

        // 최신 회차 순으로 정렬
        allDraws.sort(Comparator.comparingInt(LotteryDraw::getDrawNo).reversed());

        Map<String, Integer> rankSummary = new LinkedHashMap<>();
        rankSummary.put("1등", 0);
        rankSummary.put("2등", 0);
        rankSummary.put("3등", 0);
        rankSummary.put("4등", 0);
        rankSummary.put("5등", 0);
        rankSummary.put("낙첨", 0);

        List<DrawResult> drawResults = allDraws.stream()
                .map(draw -> {
                    Set<Integer> winSet = Set.of(
                            draw.getNum1(), draw.getNum2(), draw.getNum3(),
                            draw.getNum4(), draw.getNum5(), draw.getNum6()
                    );

                    // 일치 번호 개수 계산
                    int matchCount = (int) inputSet.stream().filter(winSet::contains).count();
                    boolean bonusMatch = inputSet.contains(draw.getBonusNum());
                    String rank = determineRank(matchCount, bonusMatch);

                    rankSummary.merge(rank, 1, Integer::sum);

                    return DrawResult.builder()
                            .drawNo(draw.getDrawNo())
                            .drawDate(draw.getDrawDate())
                            .winningNumbers(List.of(
                                    draw.getNum1(), draw.getNum2(), draw.getNum3(),
                                    draw.getNum4(), draw.getNum5(), draw.getNum6()
                            ))
                            .bonusNum(draw.getBonusNum())
                            .matchCount(matchCount)
                            .bonusMatch(bonusMatch)
                            .rank(rank)
                            .build();
                })
                .collect(Collectors.toList());

        return SimulateResponse.builder()
                .inputNumbers(new ArrayList<>(inputNumbers))
                .totalDrawCount(allDraws.size())
                .rankSummary(rankSummary)
                .drawResults(drawResults)
                .build();
    }

    /**
     * 일치 번호 수와 보너스 일치 여부로 등수를 결정한다.
     * 2등 조건: 5개 일치 + 보너스 일치 (6개 중 5개만 맞고 나머지 1개가 보너스인 경우)
     */
    private String determineRank(int matchCount, boolean bonusMatch) {
        return switch (matchCount) {
            case 6 -> "1등";
            case 5 -> bonusMatch ? "2등" : "3등";
            case 4 -> "4등";
            case 3 -> "5등";
            default -> "낙첨";
        };
    }
}
