package com.wjc.codetest.product.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
/*
 * 1. 문제: 성능 (조회 성능 저하)
 * 2. 원인: 설계 (category 필드에 인덱스가 없어 데이터가 많아질수록 필터링 및 DISTINCT 쿼리가 느려짐)
 * 3. 개선안: @Table(indexes = @Index(name = "idx_product_category", columnList = "category"))를 추가하여 DB 레벨 조회 최적화
 * 4. 검증: ProductRepository 내 주석 참고
 */
@Getter
/*
 * 1. 문제: 설계 (캡슐화 위반)
 * 2. 원인: 코드 (Lombok @Setter로 인해 외부 계층에서 객체의 상태를 아무런 제약 없이 변경할 수 있음)
 * 3. 개선안: @Setter를 제거하고 의미가 명확한 비즈니스 메소드(예: updateProduct)를 통해 상태 변경을 도메인 내부로 응집
 */
@Setter
public class Product {

    @Id
    @Column(name = "product_id")
    /*
     * 1. 문제: 성능 및 설계 (ID 생성 전략 모호성)
     * 2. 원인: 설정 (GenerationType.AUTO 사용으로 인해 DB 방언에 따라 불필요한 시퀀스 테이블 조회 쿼리 등이 발생할 수 있음)
     * 4. 개선안: 일반적으로 권장되는 GenerationType.IDENTITY를 명시하여 최적화된 auto_increment 기능 활용
     * 4. 검증: Product 저장 시 Hibernate SQL 로그 확인
     *    - AUTO 전략: hibernate_sequence 조회 쿼리 발생
     *      Hibernate:
     *          select
     *              next value for product_seq
     *      Hibernate:
     *          insert
     *          into
     *              product
     *              (category, name, product_id)
     *          values
     *              (?, ?, ?)
     *    - IDENTITY 전략: insert 쿼리만 수행됨
     *      Hibernate:
     *          insert
     *          into
     *              product
     *              (category, name, product_id)
     *          values
     *              (?, ?, default)
     */
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    /*
     * 1. 문제: 가독성 및 유지보수성 (기계적인 반복 코드)
     * 2. 원인: 코드 (Lombok 어노테이션 활용이 가능함에도 수동으로 기본 생성자와 전체 인자 생성자를 직접 작성함)
     * 3. 개선안:
     *    - 수동 생성자를 삭제하고 Lombok의 @NoArgsConstructor, @AllArgsConstructor 활용
     *    - JPA 명세에 따라 기본 생성자는 외부 호출을 제한하기 위해 @NoArgsConstructor(access = AccessLevel.PROTECTED)로 설정하여 안정성 확보
     */

    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /*
     * 1. 문제: 가독성 및 유지보수성 (코드 중복)
     * 2. 원인: 코드 (클래스 레벨의 Lombok @Getter가 존재함에도 수동 getter 메소드가 중복으로 작성됨)
     * 3. 개선안: 수동 작성된 getCategory(), getName() 메소드를 삭제하여 코드 간결성 및 유지보수성 향상
     */

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
