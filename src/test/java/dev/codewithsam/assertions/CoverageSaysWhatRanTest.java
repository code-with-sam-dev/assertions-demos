package dev.codewithsam.assertions;

import dev.codewithsam.refunds.Order;
import dev.codewithsam.refunds.RefundPolicy;
import dev.codewithsam.refunds.RefundResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The coverage beat, and it is the bridge back to the TDD episode.
 *
 * Both tests below drive the SAME path through RefundPolicy.refund and produce
 * the SAME green gutter. One of them would notice if that method returned the
 * wrong amount. The other would not.
 *
 * Coverage can tell you execution reached RefundPolicy.java:35. It cannot tell
 * you whether your test would notice that line returning the wrong number.
 */
class CoverageSaysWhatRanTest {

    private final RefundPolicy policy = new RefundPolicy();

    @Test
    @DisplayName("100% of the lines, 0% of the meaning")
    void weakAssertionWithFullCoverage() {
        RefundResult result = policy.refund(Order.captured("A-1", 10_000), "k1", 3_000);

        // Exercises every line of the happy path. Checks nothing about refunds.
        assertNotNull(result);
    }

    @Test
    @DisplayName("the same lines, and an assertion that would notice a wrong answer")
    void preciseAssertionWithTheSameCoverage() {
        RefundResult result = policy.refund(Order.captured("A-1", 10_000), "k1", 3_000);

        assertThat(result.refundedCents()).isEqualTo(3_000);
        assertThat(result.order().refundableCents()).isEqualTo(7_000);
    }
}
