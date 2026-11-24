package org.dodds.nfrapi.org;

import lombok.RequiredArgsConstructor;
import org.dodds.nfrapi.common.TokenHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InvitationService {

    private final InvitationMapper invitationMapper;
    private final InvitationRepository invitationRepository;
    private final OrgRepository orgRepository;
    private final TokenHasher tokenHasher;

    @Transactional
    public String createInvitation(CreateInvitationRequest request, String senderEmail) {
        if(! orgRepository.existsById(request.getOrgId())) {
            throw new OrganizationNotFoundException();
        }
        String token = tokenHasher.generateToken();
        var invitation = invitationMapper.toEntity(request);
        invitation.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        invitation.setTokenHash(tokenHasher.hash(token));
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiration(LocalDateTime.now().plusDays(7));
        invitation.setSenderEmail(senderEmail.trim().toLowerCase(Locale.ROOT));
        invitationRepository.save(invitation);
        return token;
    }

}
