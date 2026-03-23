package com.OBE.workflow.feature.school_year;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolYearService {
    private final SchoolYearRepository schoolYearRepository;

    @Transactional(readOnly = true)
    public Page<SchoolYear> getSchoolYears(Pageable pageable, String idFilter) {
        Specification<SchoolYear> spec = Specification.where(SchoolYearSpecification.hasId(idFilter));
        return schoolYearRepository.findAll(spec, pageable);
    }
}
