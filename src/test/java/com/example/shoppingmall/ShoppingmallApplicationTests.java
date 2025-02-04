package com.example.shoppingmall;

import com.example.shoppingmall.entitiy.Coupon;
import com.example.shoppingmall.entitiy.IssuedCoupon;
import com.example.shoppingmall.service.CouponIssueService;
import com.example.shoppingmall.service.CouponService;
import com.example.shoppingmall.service.IssuedCouponService;
import com.example.shoppingmall.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShoppingmallApplicationTests {



}
//
//@SpringBootTest
//class CouponIssueServiceTest {
//	@Autowired
//	private CouponIssueService couponIssueService;
//
//	@Autowired
//	private CouponService couponService;
//
//	@Autowired
//	private RedisTemplate<String, String> redisCouponTemplate;
//
//	@Test
//	@DisplayName("동시에 100명이 쿠폰을 발급받으려 할 때 정확히 설정된 수량만큼만 발급되어야 한다")
//	void concurrentCouponIssueTest() throws InterruptedException {
//		// Given
//		int numberOfThreads = 100;
//		int couponQuantity = 50;
//
//		// 테스트용 쿠폰 생성
//		Coupon coupon = new Coupon();
//		coupon.setQuantity(couponQuantity);
//		coupon.setEndDate(LocalDateTime.now().plusDays(1));
//		coupon.setStartDate(LocalDateTime.now().minusDays(1));
//		coupon.setName("동시");
//		coupon.setMaxOrderAmount(100000);
//		coupon.setDiscount(20);
//		coupon.setMinOrderAmount(1000);
//		coupon.setSpecial(true);
//		Coupon savedCoupon = couponService.createCoupon(coupon);
//
//		// When
//		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//		CountDownLatch latch = new CountDownLatch(numberOfThreads);
//		AtomicInteger successCount = new AtomicInteger();
//
//		for (int i = 0; i < numberOfThreads; i++) {
//			long memberId = i + 1L; // 테스트용 회원 ID
//			executorService.submit(() -> {
//				try {
//					if (couponIssueService.issueCoupon(savedCoupon.getId(), memberId)) {
//						successCount.incrementAndGet();
//					}
//				} finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		latch.await();
//
//		// Then
//		assertEquals(couponQuantity, successCount.get());
//
//		// Redis에 저장된 실제 수량 확인
//		String quantityKey = "coupon:quantity:" + savedCoupon.getId();
//		String remainingQuantity = redisCouponTemplate.opsForValue().get(quantityKey);
//		assertEquals("0", remainingQuantity);
//	}
//
//	@Test
//	@DisplayName("동일한 사용자가 동시에 여러 번 요청할 경우 한 번만 발급되어야 한다")
//	void duplicateRequestTest() throws InterruptedException {
//		// Given
//		int numberOfThreads = 10;
//		Long memberId = 3L;
//
//		Coupon coupon = new Coupon();
//		coupon.setQuantity(5);
//		coupon.setEndDate(LocalDateTime.now().plusDays(1));
//		coupon.setStartDate(LocalDateTime.now().minusDays(1));
//		coupon.setName("동시3");
//		coupon.setMaxOrderAmount(100000);
//		coupon.setDiscount(20);
//		coupon.setMinOrderAmount(1000);
//		coupon.setSpecial(true);
//		Coupon savedCoupon = couponService.createCoupon(coupon);
//
//		// When
//		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//		CountDownLatch latch = new CountDownLatch(numberOfThreads);
//		AtomicInteger successCount = new AtomicInteger();
//
//		for (int i = 0; i < numberOfThreads; i++) {
//			executorService.submit(() -> {
//				try {
//					if (couponIssueService.issueCoupon(savedCoupon.getId(), memberId)) {
//						successCount.incrementAndGet();
//					}
//				} finally {
//					latch.countDown();
//				}
//			});
//		}
//
//		latch.await();
//
//		// Then
//		assertEquals(1, successCount.get(), "동일 사용자는 한 번만 발급받아야 합니다");
//
//		executorService.shutdown();
//	}
//}
//
//
////
////@SpringBootTest
////class CouponIssueIntegrationTest {
////    @Autowired
////    private CouponIssueService couponIssueService;
////
////    @Autowired
////    private IssuedCouponService issuedCouponService;
////
////    @Autowired
////    private CouponService couponService;
////
////    @Test
////    @DisplayName("쿠폰 발급 시 Redis, Kafka, DB 전체 플로우가 정상 동작해야 한다")
////    void fullCouponIssueFlowTest() throws InterruptedException {
////        // Given
////        Coupon coupon = new Coupon();
////        coupon.setQuantity(100);
////        coupon.setSpecial(true);
////        Coupon savedCoupon = couponService.createCoupon(coupon);
////
////        // When
////        boolean issueResult = couponIssueService.issueCoupon(savedCoupon.getId(), 1L);
////
////        // Then
////        assertTrue(issueResult);
////
////        // Kafka 이벤트가 처리되고 DB에 저장되기를 기다림
////        Thread.sleep(1000);
////
////        // DB에 저장된 발급 내역 확인
////        IssuedCoupon issuedCoupon = issuedCouponService.findMemberAndCoupon(1L, savedCoupon.getId());
////        assertNotNull(issuedCoupon);
////        assertFalse(issuedCoupon.isUsed());
////    }
////}
////
////
////
//@SpringBootTest
//class CouponIssueServiceTest2 {
//	@Autowired
//	private CouponIssueService couponIssueService;
//
//	private static final Logger log = LoggerFactory.getLogger(CouponIssueServiceTest2.class);
//
//
//	@Autowired
//	private CouponService couponService;
//
//	@Autowired
//	private RedisTemplate<String, String> redisCouponTemplate;
//
//	@Test
//	@DisplayName("동시에 100명이 쿠폰을 발급받으려 할 때 정확히 설정된 수량만큼만 발급되어야 한다")
//	void concurrentCouponIssueTest() throws InterruptedException, ExecutionException {
//		// Given
//		int numberOfThreads = 100;
//		int couponQuantity = 50;
//
//		Coupon coupon = new Coupon();
//		coupon.setQuantity(couponQuantity);
//		coupon.setEndDate(LocalDateTime.now().plusDays(1));
//		coupon.setStartDate(LocalDateTime.now().minusDays(1));
//		coupon.setName("동시2");
//		coupon.setMaxOrderAmount(100000);
//		coupon.setDiscount(20);
//		coupon.setMinOrderAmount(1000);
//		coupon.setSpecial(true);
//		Coupon savedCoupon = couponService.createCoupon(coupon);
//
//		// When
//		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//		CountDownLatch readyLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 준비될 때까지 대기
//		CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호를 기다림
//		CountDownLatch doneLatch = new CountDownLatch(numberOfThreads); // 완료 대기
//
//		AtomicInteger successCount = new AtomicInteger();
//		List<Future<Boolean>> futures = new ArrayList<>();
//		Set<Long> successfulMemberIds = Collections.synchronizedSet(new HashSet<>());
//
//		// 모든 스레드를 생성하고 동시 실행을 위해 대기
//		for (int i = 0; i < numberOfThreads; i++) {
//			long memberId = i + 1L;
//			futures.add(executorService.submit(() -> {
//				readyLatch.countDown(); // 스레드 준비 완료
//				startLatch.await(); // 시작 신호를 기다림
//
//				try {
//					boolean result = couponIssueService.issueCoupon(savedCoupon.getId(), memberId);
//					if (result) {
//						successCount.incrementAndGet();
//						successfulMemberIds.add(memberId);
//					}
//					return result;
//				} finally {
//					doneLatch.countDown();
//				}
//			}));
//		}
//
//		readyLatch.await(); // 모든 스레드가 준비될 때까지 대기
//		startLatch.countDown(); // 모든 스레드 동시 실행
//		doneLatch.await(); // 모든 스레드 완료 대기
//
//		// Then
//		assertEquals(couponQuantity, successCount.get(), "정확히 50개의 쿠폰만 발급되어야 합니다");
//
//		// Redis 수량 확인
//		String quantityKey = "coupon:quantity:" + savedCoupon.getId();
//		String remainingQuantity = redisCouponTemplate.opsForValue().get(quantityKey);
//		assertEquals("0", remainingQuantity, "Redis의 남은 수량이 0이어야 합니다");
//
//		// 중복 발급 확인
//		assertEquals(successCount.get(), successfulMemberIds.size(),
//				"동일한 사용자에게 중복 발급되지 않아야 합니다");
//
//		// 결과 로깅
//		log.info("성공한 회원 ID들: {}", successfulMemberIds);
//		log.info("총 발급 성공 수: {}", successCount.get());
//
//		executorService.shutdown();
//	}
//}
