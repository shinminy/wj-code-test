package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {
    /*
     * 1. 문제: 설계 (입력값 검증 부재)
     * 2. 원인: 설계 (CreateProductRequest DTO에 필드 제약 조건이 없고 컨트롤러에서 @Valid 미사용)
     * 3. 개선안: DTO 필드에 @NotBlank 등 적용 및 컨트롤러 파라미터에 @Valid 선언하여 데이터 무결성 확보
     */
    private String category;
    private String name;

    /*
     * 1. 문제: 가독성 및 유지보수성 (기계적인 반복 코드)
     * 2. 원인: 코드 (Lombok 어노테이션 활용이 가능함에도 수동으로 각 필드 조합별 생성자를 중복하여 작성함)
     * 3. 개선안:
     *    - 사용하지 않는 생성자와 수동으로 작성된 생성자를 삭제하고 Lombok의 @AllArgsConstructor 활용
     *    - 다양한 필드 조합 생성이 필요할 경우 @Builder 사용
     */

    public CreateProductRequest(String category) {
        this.category = category;
    }

    public CreateProductRequest(String category, String name) {
        this.category = category;
        this.name = name;
    }
}

