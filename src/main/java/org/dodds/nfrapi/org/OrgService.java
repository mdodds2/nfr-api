package org.dodds.nfrapi.org;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrgService {

    private final OrgRepository orgRepository;
    private final OrgMembershipRepository membershipRepository;
    private final OrgMapper orgMapper;

    public OrgDto getOrganization(UUID orgId, UUID userId) {
        if (!membershipRepository.existsByOrganizationIdAndUserId(orgId, userId)) {
            throw new OrganizationNotFoundException();
        }
        return orgRepository.findById(orgId)
                .map(orgMapper::toDto)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public List<OrgDto> getAllOrganizations(UUID userId, boolean isPlatformAdmin, String sortBy) {

        var field = "slug".equals(sortBy) ? "slug" : "name";
        var property = isPlatformAdmin ? field : "organization." + field;

        return isPlatformAdmin
                ? orgRepository.findAll(Sort.by(property)).stream().map(orgMapper::toDto).toList()
                : membershipRepository.findByUserId(userId, Sort.by(property)).stream()
                  .map(OrgMembership::getOrganization)
                  .map(orgMapper::toDto)
                  .toList();
    }

}
