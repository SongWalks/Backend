package com.sookmyung.swapclass.domain.inquiry.repository;

import com.sookmyung.swapclass.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
