package io.ztidentity.javaagent;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageBuilder;
import io.opentelemetry.api.baggage.BaggageEntry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.*;
import java.util.logging.Logger;

/**
 * See <a href="https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/context/api-propagators.md">
 * OpenTelemetry Specification</a> for more information about Propagators.
 *
 * @see AuthorizationPropagatorProvider
 */
public class AuthorizationPropagator implements TextMapPropagator {

  static final String AUTH_HEADER_KEY = "Authorization";
  static final String BAGGAGE_AUTH_KEY = "ZTI-Authorization";

  private static final Logger logger = Logger.getLogger(AuthorizationPropagator.class.getName());

  private static final Collection<String> FIELDS = Collections.singletonList(AUTH_HEADER_KEY);

  public AuthorizationPropagator() {
  }

  @Override
  public Collection<String> fields() {
    //System.out.println("** called fields()");
    logger.finer("called fields()");
    return FIELDS;
  }

  @Override
  public <C> void inject(Context context, C carrier, TextMapSetter<C> setter) {
    //System.out.println("** called inject()");
    logger.finer("called inject()");
    Objects.requireNonNull(context, "context");
    Objects.requireNonNull(setter, "setter");

    Map<String, BaggageEntry> bag = Baggage.fromContext(context).asMap();
    if (bag.containsKey(BAGGAGE_AUTH_KEY)) {
      String outgoingAuth = bag.get(BAGGAGE_AUTH_KEY).getValue();
      setter.set(carrier, AUTH_HEADER_KEY, outgoingAuth);
      logger.fine("Injected header: " + AUTH_HEADER_KEY);
    }
  }

  @Override
  public <C> Context extract(Context context, C carrier, TextMapGetter<C> getter) {
    //System.out.println("** called extract()");
    logger.finer("called extract()");
    Objects.requireNonNull(getter, "getter");

    BaggageBuilder builder = Baggage.builder();
    String incomingAuth = getter.get(carrier, AUTH_HEADER_KEY);
    if (incomingAuth != null) {
      builder.put(BAGGAGE_AUTH_KEY, incomingAuth);
      logger.fine("Extracted header: " + AUTH_HEADER_KEY);
    }

    return context.with(builder.build());
  }
}