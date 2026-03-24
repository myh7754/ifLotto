package com.example.iflofftob.draw;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로또 회차 조회 API 컨트롤러.
 * 최신 회차 및 특정 회차 당첨 정보를 제공한다.
 */
@RestController
@RequestMapping("/api/draws")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;

    /** 최신 회차 당첨 번호를 반환한다. */
    @GetMapping("/latest")
    public ResponseEntity<DrawResponse> getLatestDraw() {
        return ResponseEntity.ok(drawService.getLatestDraw());
    }

    /** 특정 회차 번호의 당첨 정보를 반환한다. */
    @GetMapping("/{drawNo}")
    public ResponseEntity<DrawResponse> getDraw(@PathVariable int drawNo) {
        return ResponseEntity.ok(drawService.getDraw(drawNo));
    }
}
