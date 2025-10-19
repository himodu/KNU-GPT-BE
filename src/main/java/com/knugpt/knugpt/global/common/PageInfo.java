package com.knugpt.knugpt.global.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Schema(description = "페이지네이션 정보")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageInfo extends SelfValidating<PageInfo> {
    @NotNull
    Integer totalPage;

    @NotNull
    Integer currentPage;

    @NotNull
    Integer totalCnt;

    @NotNull
    Integer currentCnt;

    @Builder
    public PageInfo(Integer totalPage, Integer currentPage, Integer totalCnt, Integer currentCnt) {
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.totalCnt = totalCnt;
        this.currentCnt = currentCnt;
        validateSelf();
    }
    public static PageInfo of(Page<?> postPage) {
        return PageInfo.builder()
                .totalPage(postPage.getTotalPages())
                .currentPage(postPage.getNumber())
                .totalCnt((int) postPage.getTotalElements())
                .currentCnt(postPage.getNumberOfElements())
                .build();
    }

    public static PageInfo emptyOf() {
        return PageInfo.builder()
                .totalPage(0)
                .currentPage(0)
                .totalCnt(0)
                .currentCnt(0)
                .build();
    }
}
