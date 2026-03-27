package com.OBE.workflow.feature.semester;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SemesterSpecification {

    public static Specification<Semester> filterBy(SemesterFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filter.getId()));
            }

            if (filter.getTerm() != null) {
                predicates.add(criteriaBuilder.equal(root.get("term"), filter.getTerm()));
            }

            if (StringUtils.hasText(filter.getAcademicYear())) {
                // Sử dụng LIKE để tìm kiếm gần đúng năm học (VD: "2024" tìm được "2024-2025")
                predicates.add(criteriaBuilder.like(root.get("academicYear"), "%" + filter.getAcademicYear() + "%"));
            }

            if (filter.getStartDate() != null) {
                // Lọc những học kỳ bắt đầu từ ngày này trở về sau
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDate()));
            }

            if (filter.getEndDate() != null) {
                // Lọc những học kỳ kết thúc trước ngày này
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), filter.getEndDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
