package com.wjc.codetest.product.repository;

import com.wjc.codetest.product.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
     * 1. 문제: 성능 (대량 데이터 환경에서의 잠재적 조회 성능 저하)
     * 2. 원인: 설계 (category 필드에 인덱스가 없어 풀 테이블 스캔으로 수행됨)
     * 3. 개선안: 엔티티 레벨에 category 인덱스를 추가하여 인덱스 스캔을 유도
     * 4. 검증: H2 인메모리 DB에 테스트 데이터 적재 후 H2 Console(http://localhost:8080/h2-console)에서 EXPLAIN 실행 계획 확인
     *         (H2 인메모리 특성상 실행 시간 비교는 의미 없다고 판단하여 제외)
     *    - 인덱스 적용 전: 실행 계획에서 tableScan 확인
     *      - findAllByCategory
     *        SELECT
     *            "PUBLIC"."PRODUCT"."PRODUCT_ID",
     *            "PUBLIC"."PRODUCT"."CATEGORY",
     *            "PUBLIC"."PRODUCT"."NAME"
     *        FROM "PUBLIC"."PRODUCT"
     *            /* PUBLIC.PRODUCT.tableScan * /
     *            WHERE "CATEGORY" = 'category1'
     *      - findDistinctCategories
     *        SELECT DISTINCT
     *            "CATEGORY"
     *        FROM "PUBLIC"."PRODUCT"
     *            /* PUBLIC.PRODUCT.tableScan * /
     *    - 인덱스 적용 후: IDX_PRODUCT_CATEGORY 스캔으로 변경됨
     *      - findAllByCategory
     *        SELECT
     *            "PUBLIC"."PRODUCT"."PRODUCT_ID",
     *            "PUBLIC"."PRODUCT"."CATEGORY",
     *            "PUBLIC"."PRODUCT"."NAME"
     *        FROM "PUBLIC"."PRODUCT"
     *            /* PUBLIC.IDX_PRODUCT_CATEGORY: CATEGORY = 'category1' * /
     *            WHERE "CATEGORY" = 'category1'
     *      - findDistinctCategories
     *        SELECT DISTINCT
     *            "CATEGORY"
     *        FROM "PUBLIC"."PRODUCT"
     *            /* PUBLIC.IDX_PRODUCT_CATEGORY * /
     *        /* distinct * /
     */

    /*
     * 1. 문제: 가독성 및 유지보수성 (매개변수 명명 불일치)
     * 2. 원인: 코드 (findAllByCategory 메소드에서 카테고리 값을 받는 파라미터명이 String 'name'으로 되어 있어 개발자에게 혼동을 줌)
     * 3. 개선안: 파라미터명을 String 'category'로 수정하여 메소드 의도와 매개변수의 의미를 일치시킴
     */
    Page<Product> findAllByCategory(String name, Pageable pageable);

    /*
     * 1. 문제: 설계 및 유지보수성 (불필요한 JPQL 하드코딩)
     * 2. 원인: 코드 (Spring Data JPA의 메소드 명명 규칙으로 해결 가능한 단순 조회에 @Query를 사용하여 JPQL 하드코딩)
     * 3. 개선안:
     *    - 단순 중복 제거 조회는 findDistinctCategoryBy()와 같은 표준 명명 규칙을 사용하여 @Query 제거
     *    - 메소드명만으로 의도가 명확하지 않거나, 향후 복합적인 동적 필터링이 필요한 경우 Querydsl 도입 권장 (타입 안정성, 가독성, 확장성)
     */
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}
