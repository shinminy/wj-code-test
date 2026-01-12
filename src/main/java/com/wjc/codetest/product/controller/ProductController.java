package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * 1. 문제: 설계 (REST API 설계 원칙 미준수)
 * 2. 원인: 설계 (리소스 기반이 아닌 동사 위주의 URI 경로 및 부적절한 HTTP 메소드 사용)
 * 3. 개선안:
 *    - 클래스 상단 @RequestMapping("/products")로 경로 통일
 *    - 상세 조회: GET /{productId}
 *    - 생성: POST (경로 없음)
 *    - 수정: PUT /{productId}
 *    - 삭제: DELETE /{productId}
 *    - 목록 조회: GET (쿼리 파라미터 활용)
 *    - 상품 목록 조회: GET (경로 없음)
 *    - 카테고리 목록 조회: GET /categories
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 ResponseEntity 바디에 직접 담아 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 ResponseEntity 바디에 직접 담아 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    @PostMapping(value = "/create/product")
    public ResponseEntity<Product> createProduct(
            /*
             * 1. 문제: 설계 (입력값 검증 부재)
             * 2. 원인: 설계 (CreateProductRequest DTO에 필드 제약 조건이 없고 컨트롤러에서 @Valid 미사용)
             * 3. 개선안: DTO 필드에 @NotBlank 등 적용 및 컨트롤러 파라미터에 @Valid 선언하여 데이터 무결성 확보
             */
            @RequestBody CreateProductRequest dto
    ){
        Product product = productService.create(dto);
        return ResponseEntity.ok(product);
    }

    /*
     * 1. 문제: 설계 (의미에 맞지 않는 반환 타입)
     * 2. 원인: 코드 (삭제 성공 여부를 Boolean으로 반환하여 REST API의 상태 코드 활용 미흡)
     * 3. 개선안: 반환 타입을 ResponseEntity<Void>로 변경하고 성공 시 HTTP 204 No Content 반환
     */
    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 ResponseEntity 바디에 직접 담아 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(
            /*
             * 1. 문제: 설계 (데이터 중복 수신 및 입력값 검증 부재)
             * 2. 원인: 설계 (수정 대상 ID를 경로 변수와 DTO 필드 양쪽에서 중복 수신하며, 필드 제약 조건이 없고 컨트롤러에서 @Valid 미사용)
             * 3. 개선안:
             *    - 컨트롤러에서 경로 변수(@PathVariable)로 ID를 받고 DTO에서는 수정 데이터만 수신하도록 id 필드 제거
             *    - DTO 필드에 @NotBlank 등 적용 및 컨트롤러 파라미터에 @Valid 선언하여 데이터 무결성 확보
             */
            @RequestBody UpdateProductRequest dto
    ){
        Product product = productService.update(dto);
        return ResponseEntity.ok(product);
    }

    /*
     * 1. 문제: 보안 및 설계 (비표준 응답 구조 및 엔티티 직접 노출)
     * 2. 원인: 코드 (자체 정의한 리스트 응답 구조를 사용하고, Response DTO 내부에 JPA 엔티티를 직접 포함)
     * 3. 개선안:
     *    - 커스텀 ProductListResponse 대신 Spring Data의 Page<ProductResponse>를 반환하여 페이징 메타데이터 표준 규격 준수
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(
            /*
             * 1. 문제: 설계 (HTTP 메소드 의미 오용 및 비표준 페이징)
             * 2. 원인: 설계 (GET 요청임에도 @RequestBody를 사용하고 커스텀 페이징 DTO를 사용함)
             * 3. 개선안:
             *    - 검색 조건(category)은 쿼리 파라미터로, 페이징은 표준 Pageable 객체를 사용하여 표준 규격 준수
             *    - 정렬 조건을 고정하고 싶으면 page, size도 쿼리 파라미터로 받음
             *    - 쿼리 파라미터로 직접 받는 필드 검증을 위해 필요 시 컨트롤러에 @Validated 검토
             */
            @RequestBody GetProductListRequest dto
    ){
        Page<Product> productList = productService.getListByCategory(dto);
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    /*
     * 1. 문제: 설계 (불명확한 메소드명 및 응답 구조의 확장성 부족)
     * 2. 원인: 코드 (메소드명이 실제 반환 데이터와 일치하지 않고, List<String>을 직접 반환하여 향후 메타데이터 추가가 어려운 구조)
     * 3. 개선안:
     *    - 메소드명을 getCategories로 변경하여 실제 카테고리 목록을 조회한다는 의도를 명확히 전달
     *    - 서비스에서 category 목록을 가진 CategoryListResponse DTO를 생성하여 반환 (확장성)
     */
    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}