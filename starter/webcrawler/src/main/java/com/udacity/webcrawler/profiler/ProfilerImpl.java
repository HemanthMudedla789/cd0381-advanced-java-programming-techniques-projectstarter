package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

final class ProfilerImpl implements Profiler {
  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);
    Objects.requireNonNull(delegate);

    // Check if the class has any profiled methods
    boolean hasProfiledMethods = false;
    for (Method method : klass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Profiled.class)) {
        hasProfiledMethods = true;
        break;
      }
    }

    if (!hasProfiledMethods) {
      throw new IllegalArgumentException(
              String.format("No @Profiled methods found in %s", klass.getName()));
    }

    // Create a proxy instance that wraps the delegate
    Object proxy = Proxy.newProxyInstance(
            klass.getClassLoader(),
            new Class<?>[] {klass},
            new ProfilingMethodInterceptor(clock, delegate, state));

    return klass.cast(proxy);
  }

  @Override
  public void writeData(Path path) throws IOException {
    // Write data to the specified path, appending if the file exists
    try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
            StandardOpenOption.APPEND)) {
      writeData(writer);
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}