package com.toyota.cashier.DAO;

import com.toyota.cashier.Domain.Coupons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.toyota.cashier")
@ActiveProfiles("test")
public class CouponRepositoryTest {

    @Autowired
    private CouponsRepository couponsRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Coupons coupon1;
    private Coupons coupon2;

    @BeforeEach
    public void setUp() {
        coupon1 = new Coupons();
        coupon1.setDiscountType(Coupons.DiscountType.PERCENTAGE);
        coupon1.setDiscountValue(10.0);
        coupon1.setExpirationDate(LocalDateTime.now());

        coupon2 = new Coupons();
        coupon2.setDiscountValue(20.4);
        coupon2.setDiscountType(Coupons.DiscountType.FIXED);
        coupon1.setExpirationDate(LocalDateTime.now());

        entityManager.persistAndFlush(coupon1);
        entityManager.persistAndFlush(coupon2);
    }

    @Test
    public void whenFindById_thenReturnCoupon() {
        // when
        Optional<Coupons> foundCoupon = couponsRepository.findById(coupon1.getId());

        // then
        assertThat(foundCoupon).isPresent();
        assertThat(foundCoupon.get().getDiscountType()).isEqualTo(Coupons.DiscountType.PERCENTAGE);
    }

    @Test
    public void whenFindAll_thenReturnAllCoupons() {
        // when
        Iterable<Coupons> allCoupons = couponsRepository.findAll();

        // then
        assertThat(allCoupons).hasSize(2).extracting(Coupons::getDiscountValue).contains(20.4);
    }

    @Test
    public void whenSave_thenCouponIsPersisted() {
        // given
        Coupons coupon = new Coupons();
        coupon.setDiscountType(Coupons.DiscountType.FIXED);
        coupon.setDiscountValue(30.0);

        // when
        Coupons savedCoupon = couponsRepository.save(coupon);

        // then
        assertThat(savedCoupon).isNotNull();
        assertThat(savedCoupon.getId()).isNotNull();

    }

    @Test
    public void whenDelete_thenCouponIsDeleted() {
        // given
        Long couponId = coupon1.getId();

        // when
        couponsRepository.deleteById(couponId);

        // then
        Optional<Coupons> deletedCoupon = couponsRepository.findById(couponId);
        assertThat(deletedCoupon).isNotPresent();
    }
}
