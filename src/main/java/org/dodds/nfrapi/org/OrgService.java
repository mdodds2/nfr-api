package org.dodds.nfrapi.org;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//    public List<OrgDto> getAllOrganization(UUID orgId, UUID userId) {
//        if (!membershipRepository.existsByOrganizationIdAndUserId(orgId, userId)) {
//            throw new OrganizationNotFoundException();
//        }
//        return orgRepository.findById(orgId)
//                .map(orgMapper::toDto)
//                .orElseThrow(OrganizationNotFoundException::new);
//    }
}
