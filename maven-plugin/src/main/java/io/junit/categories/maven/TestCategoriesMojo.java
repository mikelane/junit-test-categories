package io.junit.categories.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Placeholder Mojo for the test-categories Maven plugin.
 *
 * <p>This will be fully implemented in Phase 1.
 */
@Mojo(name = "configure")
public class TestCategoriesMojo extends AbstractMojo {

  /** Hermeticity enforcement mode: OFF, WARN, or STRICT. */
  @Parameter(property = "testCategories.hermeticityMode", defaultValue = "OFF")
  private String hermeticityMode;

  /** Distribution enforcement mode: OFF, WARN, or STRICT. */
  @Parameter(property = "testCategories.distributionMode", defaultValue = "OFF")
  private String distributionMode;

  @Override
  public void execute() {
    getLog().info("junit-test-categories plugin loaded (Phase 0 - placeholder)");
    getLog().info("Hermeticity mode: " + hermeticityMode);
    getLog().info("Distribution mode: " + distributionMode);
  }
}
