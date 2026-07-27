package com.sookmyung.swapclass.domain.inquiry.service;

import com.sookmyung.swapclass.domain.inquiry.dto.request.InquiryRequest;
import com.sookmyung.swapclass.domain.inquiry.entity.Inquiry;
import com.sookmyung.swapclass.domain.inquiry.repository.InquiryRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createInquiry(Long userId, InquiryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .content(request.getContent())
                .relatedReportId(request.getRelatedReportId())
                .build();

        return inquiryRepository.save(inquiry).getId();
    }
}
