package com.OBE.workflow.feature.course_version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseVersionId implements Serializable {

    // Tên thuộc tính phải khớp hoàn toàn với thuộc tính @Id trong lớp CourseVersion
    private String course;

    private Integer versionNumber;
}