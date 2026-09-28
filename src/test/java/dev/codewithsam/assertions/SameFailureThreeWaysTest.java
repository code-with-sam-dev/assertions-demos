package dev.codewithsam.assertions;

import dev.codewithsam.refunds.Order;
import dev.codewithsam.refunds.RefundPolicy;
import dev.codewithsam.refunds.RefundRejected;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ONE failing assertion, written three ways, so the FAILURE MESSAGE is what
 * gets compared rather than the syntax.
 *
 * EVERY TEST IN THIS CLASS IS MEANT TO FAIL. That is the point: the messages
 * are the evidence the video is built on, and surefire is configured with
 * testFailureIgnore so the whole set runs and every message is captured.
 *
 * NOTE THE IMPORTS AT THE TOP, because they are the thing people get wrong.
 * THREE libraries export a method called assertThat, and two of them are
 * imported here. They do not conflict only because their argument types
 * differ enough for overload resolution to pick one. Mix them carelessly in a
 * real class and you get an error that reads like nonsense.
 */
class SameFailureThreeWaysTest {

    private final RefundPolicy policy = new RefundPolicy();

    // ---------------------------------------------------------------
    // 1. A SCALAR. The gap between the three is smallest here, which is
    //    why a comparison that stops at scalars concludes there is no gap.
    // ---------------------------------------------------------------

    @Test
    @DisplayName("scalar, plain JUnit 5")
    void scalarJUnit() {
        Order order = Order.captured("A-1", 5000);
        long remaining = policy.refund(order, "k1", 1500).order().refundableCents();
        assertEquals(4000, remaining);
    }

    @Test
    @DisplayName("scalar, AssertJ")
    void scalarAssertJ() {
        Order order = Order.captured("A-1", 5000);
        long remaining = policy.refund(order, "k1", 1500).order().refundableCents();
        assertThat(remaining).isEqualTo(4000);
    }

    @Test
    @DisplayName("scalar, Hamcrest")
    void scalarHamcrest() {
        Order order = Order.captured("A-1", 5000);
        long remaining = policy.refund(order, "k1", 1500).order().refundableCents();
        assertThat(remaining, equalTo(4000L));
    }

    // ---------------------------------------------------------------
    // 2. A COLLECTION. This is where the gap is widest and where the
    //    argument for AssertJ actually lives.
    // ---------------------------------------------------------------

    @Test
    @DisplayName("collection, plain JUnit 5")
    void collectionJUnit() {
        List<String> keys = keysAfterThreeRefunds();
        assertEquals(List.of("k1", "k2", "k9"), keys);
    }

    @Test
    @DisplayName("collection, AssertJ")
    void collectionAssertJ() {
        List<String> keys = keysAfterThreeRefunds();
        assertThat(keys).containsExactly("k1", "k2", "k9");
    }

    @Test
    @DisplayName("collection, Hamcrest")
    void collectionHamcrest() {
        List<String> keys = keysAfterThreeRefunds();
        assertThat(keys, contains("k1", "k2", "k9"));
    }

    // ---------------------------------------------------------------
    // 3. AN EXCEPTION MESSAGE. A refund larger than the captured amount.
    // ---------------------------------------------------------------

    @Test
    @DisplayName("exception message, plain JUnit 5")
    void exceptionJUnit() {
        RefundRejected e = assertThrows(RefundRejected.class,
            () -> policy.refund(Order.captured("A-1", 5000), "k1", 6000));
        assertEquals("refund 6000 exceeds captured 5000 on A-1", e.getMessage());
    }

    @Test
    @DisplayName("exception message, AssertJ")
    void exceptionAssertJ() {
        RefundRejected e = assertThrows(RefundRejected.class,
            () -> policy.refund(Order.captured("A-1", 5000), "k1", 6000));
        assertThat(e).hasMessage("refund 6000 exceeds captured 5000 on A-1");
    }

    private List<String> keysAfterThreeRefunds() {
        Order order = Order.captured("A-1", 5000);
        order = policy.refund(order, "k1", 1000).order();
        order = policy.refund(order, "k2", 1000).order();
        order = policy.refund(order, "k3", 1000).order();
        return order.attempts().stream().map(a -> a.idempotencyKey()).toList();
    }
}
