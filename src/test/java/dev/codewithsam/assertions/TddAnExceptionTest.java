package dev.codewithsam.assertions;

import dev.codewithsam.refunds.Order;
import dev.codewithsam.refunds.RefundAttempt;
import dev.codewithsam.refunds.RefundPolicy;
import dev.codewithsam.refunds.RefundRejected;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test driving a FAILURE PATH, which is where a weak assertion is most
 * dangerous because the weak version looks thorough.
 *
 * THE THREE DECISIONS TDD FORCES on an exception, and all three have to be made
 * before there is a throw site to copy them from:
 *   the TYPE the caller catches,
 *   the MESSAGE a support agent has to act on,
 *   the DATA a caller can branch on.
 *
 * Written afterwards, you copy whatever string the implementation happened to
 * produce, which is how an error that says "exceeds refundable" ends up
 * describing a replay.
 */
class TddAnExceptionTest {

    private final RefundPolicy policy = new RefundPolicy();

    /** 10,000 captured, 6,000 already refunded. 4,000 is left. */
    private static final Order ORDER = new Order("A-1", 10_000, List.of(
        RefundAttempt.succeeded("k1", 6_000)));

    @Test
    @DisplayName("weak: green for ANY refund rejection, including the wrong one")
    void weakExceptionAssertion() {
        // Passes whether the refusal is about the balance, the amount, the
        // capture, or a replay misdiagnosed as any of those.
        assertThrows(RefundRejected.class,
            () -> policy.refund(ORDER, "k9", 6_000));
    }

    @Test
    @DisplayName("precise: the type AND the message the caller actually reads")
    void preciseExceptionAssertion() {
        assertThatExceptionOfType(RefundRejected.class)
            .isThrownBy(() -> policy.refund(ORDER, "k9", 6_000))
            .withMessage("refund 6000 exceeds captured 4000 on A-1");
    }

    @Test
    @DisplayName("precise: asserting on the throwable rather than on prose")
    void assertingOnTheThrowableItself() {
        assertThatThrownBy(() -> policy.refund(ORDER, "k9", 6_000))
            .isInstanceOf(RefundRejected.class)
            .hasMessageContaining("exceeds captured")
            .hasNoCause();
    }
}
