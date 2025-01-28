package com.udacity.webcrawler.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Objects;

/**
 * Writes the result of a crawl to a JSON file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Writes the crawl result to JSON.
   *
   * @param path the file path where the JSON should be written.
   */
  public void write(Path path) throws IOException {
    Objects.requireNonNull(path);
    // Create parent directories if they don't exist
    if (path.getParent() != null) {
      Files.createDirectories(path.getParent());
    }

    try (Writer writer = Files.newBufferedWriter(path)) {
      write(writer);
    }
  }

  /**
   * Writes the crawl result to JSON.
   *
   * @param writer the destination where the JSON should be written.
   */
  public void write(Writer writer) throws IOException {
    Objects.requireNonNull(writer);
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    mapper.writeValue(writer, result);
  }
}