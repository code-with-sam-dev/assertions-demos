package dev.codewithsam.assertions;

import dev.codewithsam.refunds.Order;
import dev.codewithsam.refunds.RefundAttempt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.codewithsam.refunds.RefundAttempt.Outcome.FAILED;
import static dev.codewithsam.refunds.RefundAttempt.Outcome.SUCCEEDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The intellectual heart of the episode: a weak assertion and a precise one,
 * checking the same thing, failing on the same data.
 *
 * Failure messages are the demonstration surface; assertion semantics are the
 * actual subject. Prettier diagnostics are easy to dismiss. Whether the test
 * STATES the invariant is not.
 *
 * Every test here is meant to fail. The messages are the artefact.
 */
class WeakVersusPreciseTest {

    /** Three attempts: 6000 succeeded, 2000 refused, 1000 succeeded. */
    private static final Order ORDER = new Order("A-1", 10_000, List.of(
        RefundAttempt.succeeded("k1", 6_000),
        RefundAttempt.failed("k2", 2_000),
        RefundAttempt.succeeded("k3", 1_000)));

    @Test
    @DisplayName("weak: a boolean that says nothing about what was expected")
    void weakBooleanAssertion() {
        // The reader has to mentally execute the lambda to know the claim.
        assertTrue(ORDER.attempts().stream()
            .anyMatch(a -> a.amountCents() == 5_000));
    }

    @Test
    @DisplayName("precise: names the property, the order, and the cardinality")
    void preciseExtractingAssertion() {
        assertThat(ORDER.attempts())
            .extracting(RefundAttempt::amountCents)
            .containsExactly(6_000L, 5_000L, 1_000L);
    }

    @Test
    @DisplayName("weak: size is not the invariant, it only correlates with it")
    void weakSizeAssertion() {
        assertTrue(ORDER.attempts().size() == 4);
    }

    @Test
    @DisplayName("precise: the outcomes in order, which IS the invariant")
    void preciseOutcomeAssertion() {
        assertThat(ORDER.attempts())
            .extracting(RefundAttempt::outcome)
            .containsExactly(SUCCEEDED, SUCCEEDED, FAILED);
    }
}
