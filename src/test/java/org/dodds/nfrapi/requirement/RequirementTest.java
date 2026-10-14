package org.dodds.nfrapi.requirement;

import org.dodds.nfrapi.category.SubCategory;
import org.dodds.nfrapi.common.Priority;
import org.dodds.nfrapi.common.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RequirementTest {
    @Test
    void toStringTest() {
        var requirement = Requirement.builder()
                .id(UUID.randomUUID())
                .title("Title")
                .rationale("Rationale")
                .description("description")
                .identifier("Some Identifier")
                .source("Some Source")
                .priority(Priority.HIGH)
                .status(Status.APPROVED)
                .riskIfViolated("some risk")
                .createdMoment(LocalDateTime.now())
                .updatedMoment(LocalDateTime.now())
                .subCategory(SubCategory.builder().id(UUID.randomUUID()).name("SubCategory 1").build())
                .build();

        var result = requirement.toString();

        assertThat(result).isNotNull();
    }
}
