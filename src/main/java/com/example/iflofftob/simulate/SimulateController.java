package com.example.iflofftob.simulate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로또 번호 시뮬레이션 API 컨트롤러.
 * 입력된 6개 번호를 역대 회차와 대조하여 등수 통계를 반환한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SimulateController {

    private final SimulateService simulateService;

    /** 6개 번호를 입력받아 전체 역대 회차 시뮬레이션 결과를 반환한다. */
    @PostMapping("/simulate")
    public ResponseEntity<SimulateResponse> simulate(@Valid @RequestBody SimulateRequest request) {
        return ResponseEntity.ok(simulateService.simulate(request.getNumbers()));
    }
}
