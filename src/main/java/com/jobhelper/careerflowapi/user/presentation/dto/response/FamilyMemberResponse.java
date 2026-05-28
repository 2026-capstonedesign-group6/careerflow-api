package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.FamilyMember;

public record FamilyMemberResponse(
        Long id,
        String relation,
        String name,
        String birthYear,
        String occupation
) {
    public static FamilyMemberResponse from(FamilyMember entity) {
        return new FamilyMemberResponse(
                entity.getId(),
                entity.getRelation(),
                entity.getName(),
                entity.getBirthYear(),
                entity.getOccupation()
        );
    }
}
