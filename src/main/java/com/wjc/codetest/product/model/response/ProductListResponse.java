package com.wjc.codetest.product.model.response;

import com.wjc.codetest.product.model.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * 1. 문제: 보안 및 설계 (엔티티 직접 노출 및 불필요한 DTO 유지)
 * 2. 원인: 코드 (Response DTO 내부에 JPA 엔티티를 직접 포함하고 있으며, Spring Data의 Page 객체로 대체 가능한 비표준 응답 구조)
 * 3. 개선안:
 *    - ProductListResponse 클래스를 삭제하고 서비스에서 Page<ProductResponse> DTO를 반환하도록 표준화
 *    - 이를 통해 엔티티 구조 노출을 차단하고 페이징 메타데이터(totalPages 등)의 표준 규격 준수
 */

/**
 * <p>
 *
 * </p>
 *
 * @author : 변영우 byw1666@wjcompass.com
 * @since : 2025-10-27
 */
@Getter
@Setter
public class ProductListResponse {
    private List<Product> products;
    private int totalPages;
    private long totalElements;
    private int page;

    public ProductListResponse(List<Product> content, int totalPages, long totalElements, int number) {
        this.products = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.page = number;
    }
}
