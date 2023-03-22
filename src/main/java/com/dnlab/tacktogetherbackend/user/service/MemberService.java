package com.dnlab.tacktogetherbackend.user.service;

import com.dnlab.tacktogetherbackend.user.common.MemberRegistrationDTO;
import com.dnlab.tacktogetherbackend.user.common.MemberResponseDTO;

public interface MemberService {
    MemberResponseDTO registerMember(MemberRegistrationDTO registrationDTO);

    void validateDuplicated(String username);


}
