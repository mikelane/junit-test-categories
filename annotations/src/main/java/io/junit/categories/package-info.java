/**
 * Test size annotations for categorizing tests by execution characteristics.
 *
 * <p>This package provides four annotations for marking tests:
 *
 * <ul>
 *   <li>{@code @SmallTest} - Fast, hermetic unit tests (≤1 second)
 *   <li>{@code @MediumTest} - Integration tests with limited external access (≤5 minutes)
 *   <li>{@code @LargeTest} - End-to-end tests with full external access (≤15 minutes)
 *   <li>{@code @XLargeTest} - Long-running tests with no time limit
 * </ul>
 *
 * <p>This module has no dependencies and can be used standalone for annotation-only adoption.
 *
 * @see <a href="https://testing.googleblog.com/2010/12/test-sizes.html">Google Testing Blog: Test
 *     Sizes</a>
 */
package io.junit.categories;
