package com.sookmyung.swapclass.domain.course.dto.response;

/**
 * 학과/영역 드롭다운 옵션 1건.
 * type  = "DEPARTMENT"(학과전공) 또는 "AREA"(교양 영역). 프론트가 검색 시 어느 파라미터로 보낼지 판단.
 *         DEPARTMENT → GET /api/lectures?department={value}
 *         AREA       → GET /api/lectures?area={value}
 * value = 실제 표시/필터 값(학과명 또는 교양 영역명).
 */
public record FilterOptionResponse(
        String type,
        String value
) {
    public static FilterOptionResponse department(String value) {
        return new FilterOptionResponse("DEPARTMENT", value);
    }

    public static FilterOptionResponse area(String value) {
        return new FilterOptionResponse("AREA", value);
    }
}
