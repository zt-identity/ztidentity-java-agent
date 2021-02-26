package io.ztidentity.javaagent;

import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurablePropagatorProvider;

/**
 * Registers the custom propagator used by this example.
 *
 * @see ConfigurablePropagatorProvider
 * @see AuthorizationPropagator
 */
public class AuthorizationPropagatorProvider implements ConfigurablePropagatorProvider {
  @Override
  public TextMapPropagator getPropagator() {
    return new AuthorizationPropagator();
  }

  @Override
  public String getName() {
    return "ztiauth";
  }
}
