package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;

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
  public void writeData(Path path) {
    // If path is null, write to System.out
    if (path != null) {
      try (Writer writer = Files.newBufferedWriter(path)) {
        writeData(writer);
      } catch (IOException e) {
        throw new RuntimeException("Failed to write profile data", e);
      }
    } else {
      // Don't use try-with-resources here to avoid closing System.out
      writeData(new OutputStreamWriter(System.out));
    }
  }

  @Override
  public void writeData(Writer writer) {
    Objects.requireNonNull(writer);
    try {
      writer.write("{\n");
      writer.write("  \"startTime\": \"");
      writer.write(startTime.toString());
      writer.write("\",\n");
      writer.write("  \"results\": [\n");
      state.write(writer);
      writer.write("  ]\n");
      writer.write("}");
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException("Unable to write profile data", e);
    }
  }
}