package com.sookmyung.swapclass;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class SwapclassApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwapclassApplication.class, args);
	}

	// 서버 OS 타임존과 무관하게 애플리케이션 기본 타임존을 한국 시간(KST)으로 고정
	@PostConstruct
	public void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
