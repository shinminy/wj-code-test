package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/*
 * 1. 문제: 설계 (안정성 및 일관성)
 * 2. 원인: 설정 (@Transactional 미활용)
 * 3. 개선안: 클래스에 @Transactional(readOnly = true) 선언 및 CUD 메소드에 @Transactional 추가하여 작업의 원자성 보장 및 롤백 지원
 * 4. 검증: 예외 발생 시 롤백 여부와 save() 없이 데이터 변경 여부를 단위 테스트로 확인
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 직접 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 직접 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    public Product getProductById(Long productId) {
        /*
         * 1. 문제: 가독성 (불필요하게 장황한 Optional 처리)
         * 2. 원인: 코드 (Optional API를 활용하지 않고 명령형 조건문으로 처리)
         * 3. 개선안: Optional의 orElseThrow()를 사용하여 예외 처리 로직을 간결화
         */
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            /*
             * 1. 문제: 에러 처리 (사용자 경험 저하 및 잘못된 상태 코드 반환)
             * 2. 원인: 설계 (상품 부재 시 던져진 RuntimeException이 GlobalExceptionHandler에 의해 500 에러로 처리되어,
             *              클라이언트는 실제 원인인 404 Not Found를 인지할 수 없음)
             * 3. 개선안: ProductNotFoundException 커스텀 예외를 생성하고 @ResponseStatus(HttpStatus.NOT_FOUND)를 적용하거나
             *           Handler에서 404로 매핑
             */
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 직접 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 엔티티 변경이 API 응답에 영향을 주지 않도록 응답 전용 DTO로 계층 분리
     */
    public Product update(UpdateProductRequest dto) {
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        /*
         * 1. 문제: 가독성 및 유지보수성 (단순 전달용 변수 사용)
         * 2. 원인: 코드 (불필요한 지역 변수 사용으로 인한 코드 복잡도 증가)
         * 3. 개선안: 인라인화 (return productRepository.save(product))
         */
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    /*
     * 1. 문제: 설계 (엔티티 직접 노출로 인한 결합도 증가 및 정보 노출 위험)
     * 2. 원인: 코드 (JPA 엔티티인 Product를 직접 반환)
     * 3. 개선안:
     *    - 서비스에서 엔티티를 ProductResponse DTO로 변환하여 반환하도록 수정
     *    - 서비스의 트랜잭션 범위 내에서 지연 로딩 문제를 안전하게 처리하고, API 스펙과 내부 도메인 구조를 분리하여 보안성 확보
     */
    public Page<Product> getListByCategory(GetProductListRequest dto) {
        /*
         * 1. 문제: 가독성 및 유지보수성 (매직 스트링 사용)
         * 2. 원인: 코드 (정렬 필드명이 "category" 문자열로 하드코딩되어 있어 필드명 변경 시 런타임 에러 위험이 있음)
         * 3. 개선안:
         *    - 고정된 정렬 조건 유지 시, 정렬 조건을 상수로 관리하거나 Querydsl을 활용해 정렬 필드를 타입 안전하게 참조
         *    - 정렬 조건까지 컨트롤러로부터 Pageable을 통해 동적으로 전달받음
         */
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    /*
     * 1. 문제: 설계 (응답 구조의 확장성 부족)
     * 2. 원인: 코드 (List<String>을 직접 반환하여 향후 메타데이터 추가가 어려운 구조)
     * 3. 개선안: CategoryListResponse와 같은 래퍼 DTO를 생성 (향후 카테고리 개수 등 추가 정보 확장에 대비)
     */
    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}