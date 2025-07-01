package com.planit.infrastructure.jpa.holiday;

import com.planit.application.dto.requeset.HolidaySearchRequest;
import com.planit.domain.entity.Holiday;
import com.planit.domain.entity.QHoliday;
import com.planit.domain.model.Pagination;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QHoliday holiday = QHoliday.holiday;

    public Pagination<Holiday> searchHolidayList(HolidaySearchRequest request) {

        BooleanBuilder builder = new BooleanBuilder();

        // 필터링
        applyCountryFilter(builder, holiday, request);
        applyDateRangeFilter(builder, holiday, request);
        applyTypeFilter(builder, holiday, request);

        // 키워드
        if (request.keyword() != null && !request.keyword().isBlank()) {
            builder.and(
                    holiday.name.containsIgnoreCase(request.keyword())
                            .or(holiday.localName.containsIgnoreCase(request.keyword()))
            );
        }

        // 정렬
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        String sort = request.sort() == null ? "" : request.sort();
        switch (sort) {
            case "최신순" -> orderSpecifiers.add(holiday.date.desc());
            case "과거순" -> orderSpecifiers.add(holiday.date.asc());
            case "국가 내림차순" -> orderSpecifiers.add(holiday.countryCode.desc());
            case "국가 오름차순" -> orderSpecifiers.add(holiday.countryCode.asc());
            default -> {
                orderSpecifiers.add(holiday.countryCode.asc());
                orderSpecifiers.add(holiday.date.desc());
            }
        }

        List<Holiday> content = queryFactory
                .selectFrom(holiday)
                .where(builder)
                .offset((long) request.page() * request.size())
                .limit(request.size())
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .fetch();

        Long total = queryFactory
                .select(Wildcard.count)
                .from(holiday)
                .where(builder)
                .fetchOne();

        return Pagination.of(content, request.page(), request.size(), total != null ? total : 0L);
    }

    private void applyCountryFilter(BooleanBuilder builder, QHoliday holiday, HolidaySearchRequest request) {
        if (request.countryCode() != null) {
            builder.and(holiday.countryCode.eq(request.countryCode()));
        }
    }

    private void applyDateRangeFilter(BooleanBuilder builder, QHoliday holiday, HolidaySearchRequest request) {
        if (request.from() != null && request.to() != null) {
            builder.and(holiday.date.between(request.from(), request.to()));
        }
    }

    private void applyTypeFilter(BooleanBuilder builder, QHoliday holiday, HolidaySearchRequest request) {
        if (request.types() != null) {
            builder.and(holiday.types.containsIgnoreCase(request.types()));
        }
    }
}