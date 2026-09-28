# JUnit vs AssertJ vs Hamcrest: what the failure tells you

Companion code to the episode. One refund rule, and the same checks written with
each library so the failure messages can be compared directly.

**Most of these tests are meant to fail.** The messages they print are the
evidence. Surefire is configured with `testFailureIgnore`, so the whole set runs
and every message is shown.

    mvn test          # JDK 21+

| Test class | What it shows |
|---|---|
| `SameFailureThreeWaysTest` | one failing assertion in JUnit, AssertJ and Hamcrest, on a number and on a collection |
| `WeakVersusPreciseTest` | a boolean `assertTrue` against an assertion that names the property, the order and the count |
| `TddAnExceptionTest` | `assertThrows` green for the wrong rejection, against asserting the type and the message |
| `SoftAssertionsTest` | hard assertions stop at the first failure; `assertSoftly` reports all three |
| `CoverageSaysWhatRanTest` | two tests with identical line coverage, only one of which checks anything |

The refund rule itself is in `src/main/java/dev/codewithsam/refunds`.
