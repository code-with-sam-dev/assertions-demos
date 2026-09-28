package dev.codewithsam.assertions;

import dev.codewithsam.refunds.Order;
import dev.codewithsam.refunds.RefundAttempt;
import dev.codewithsam.refunds.RefundPolicy;
import dev.codewithsam.refunds.RefundResult;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every failure from one run, rather than the first one and a rerun.
 *
 * THE QUALIFICATION MATTERS AS MUCH AS THE FEATURE: these three are THREE OBSERVATIONS OF ONE OUTCOME, which is
 * what makes soft assertions appropriate here. Using them to cram unrelated
 * requirements into one test is failure accumulation, not test design.
 *
 * Soft assertions reduce reruns. They do not excuse incoherent scope.
 */
class SoftAssertionsTest {

    private final RefundPolicy policy = new RefundPolicy();

    private static final Order ORDER = new Order("A-1", 10_000, List.of(
        RefundAttempt.succeeded("k1", 6_000)));

    @Test
    @DisplayName("hard: stops at the first failure, so you learn one thing per run")
    void hardAssertionsStopAtTheFirst() {
        RefundResult result = policy.refund(ORDER, "k2", 3_000);

        assertEquals(4_000, result.refundedCents());
        assertEquals(1_000, result.order().refundableCents());
        assertEquals(3, result.order().attempts().size());
    }

    @Test
    @DisplayName("soft: one run, every failure, all three observations of one refund")
    void softAssertionsReportEverything() {
        RefundResult result = policy.refund(ORDER, "k2", 3_000);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.refundedCents())
                .as("amount refunded").isEqualTo(4_000);
            softly.assertThat(result.order().refundableCents())
                .as("balance left").isEqualTo(1_000);
            softly.assertThat(result.order().attempts())
                .as("attempt history").hasSize(3);
        });
    }
}
