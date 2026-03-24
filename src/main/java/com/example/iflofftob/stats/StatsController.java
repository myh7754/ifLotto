package com.example.iflofftob.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 로또 통계 API 컨트롤러.
 * 미출현 번호 등 통계 정보를 제공한다.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /** 최근 recentCount 회차에서 한 번도 출현하지 않은 번호 목록을 반환한다. */
    @GetMapping("/unappeared")
    public ResponseEntity<List<Integer>> getUnappearedNumbers(
            @RequestParam(defaultValue = "10") int recentCount) {
        return ResponseEntity.ok(statsService.getUnappearedNumbers(recentCount));
    }
}
