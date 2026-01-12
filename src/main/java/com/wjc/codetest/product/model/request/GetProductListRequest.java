package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/*
 * 1. 문제: 가독성 및 설계 (비표준 페이징 처리 및 불필요한 DTO 유지)
 * 2. 원인: 설계 (Spring Data JPA가 제공하는 표준 페이징 객체(Pageable)가 있음에도 불구하고, 동일한 역할을 하는 커스텀 DTO를 중복 생성함)
 * 3. 개선안:
 *    - GetProductListRequest 클래스를 삭제하고 컨트롤러 파라미터에서 Pageable을 직접 사용하여 표준 규격 준수
 *    - 검색 조건(category)은 별도의 쿼리 파라미터(@RequestParam)로 분리하여 역할과 책임을 명확히 함
 */

@Getter
@Setter
public class GetProductListRequest {
    private String category;
    private int page;
    private int size;
}