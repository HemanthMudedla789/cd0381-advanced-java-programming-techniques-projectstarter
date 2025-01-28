package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

final class ProfilingMethodInterceptor implements InvocationHandler {
  private final Clock clock;
  private final Object delegate;
  private final ProfilingState state;

  ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState state) {
    this.clock = Objects.requireNonNull(clock);
    this.delegate = Objects.requireNonNull(delegate);
    this.state = Objects.requireNonNull(state);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // Handle Object methods like equals(), hashCode() etc.
    if (method.getDeclaringClass().equals(Object.class)) {
      return method.invoke(delegate, args);
    }

    // Check if the method has the @Profiled annotation
    boolean isProfiled = method.isAnnotationPresent(Profiled.class);

    if (!isProfiled) {
      // If not profiled, just delegate the call
      return method.invoke(delegate, args);
    }

    // For profiled methods, record the timing
    Instant start = clock.instant();
    try {
      // Invoke the method on the delegate
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      // If the method throws an exception, rethrow the original exception
      throw e.getTargetException();
    } finally {
      // Record the duration even if an exception was thrown
      Duration duration = Duration.between(start, clock.instant());
      state.record(delegate.getClass(), method, duration);
    }
  }
}