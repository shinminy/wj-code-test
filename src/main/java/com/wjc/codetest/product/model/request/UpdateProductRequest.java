package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequest {
    /*
     * 1. 문제: 설계 (수정 대상 식별 방식의 불명확성 및 입력값 검증 부재)
     * 2. 원인: 설계 (수정 대상 ID를 요청 바디 DTO에 포함시켜 수신하여 리소스 식별이 URI가 아닌 요청 본문에 의존하고 있으며,
     *              필드 제약 조건이 없고 컨트롤러에서 @Valid를 사용하지 않음)
     * 3. 개선안:
     *    - 수정 대상 ID는 경로 변수(@PathVariable)로 명시적으로 수신하도록 변경하고, DTO에서는 수정 데이터만 수신하도록 id 필드 제거
     *    - DTO 필드에 @NotBlank 등 제약 조건을 추가하고 컨트롤러 파라미터에 @Valid 적용
     */
    private Long id;
    private String category;
    private String name;

    /*
     * 1. 문제: 가독성 및 유지보수성 (기계적인 반복 코드)
     * 2. 원인: 코드 (Lombok 어노테이션 활용이 가능함에도 수동으로 각 필드 조합별 생성자를 중복하여 작성함)
     * 3. 개선안:
     *    - 사용하지 않는 생성자와 수동으로 작성된 생성자를 삭제하고 Lombok의 @AllArgsConstructor 활용
     *    - 다양한 필드 조합 생성이 필요할 경우 @Builder 사용
     */

    public UpdateProductRequest(Long id) {
        this.id = id;
    }

    public UpdateProductRequest(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    public UpdateProductRequest(Long id, String category, String name) {
        this.id = id;
        this.category = category;
        this.name = name;
    }
}

