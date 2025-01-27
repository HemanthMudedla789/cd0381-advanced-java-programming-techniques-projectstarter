package com.udacity.webcrawler.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

import java.io.Reader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedReader;

/**
 * A loader that loads crawler configuration from JSON.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a new configuration loader that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = path;
  }

  /**
   * Loads configuration from JSON.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) {
    // Create object mapper instance
    ObjectMapper mapper = new ObjectMapper();
    // Disable auto-closing of the reader to prevent "Stream closed" errors
    mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

    try {
      // Read the JSON and map it to CrawlerConfiguration
      return mapper.readValue(reader, CrawlerConfiguration.class);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read crawler configuration", e);
    }
  }

  /**
   * Loads crawler configuration from the JSON file at the path provided to the constructor.
   *
   * @return a crawler configuration
   */
  public CrawlerConfiguration load() {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return read(reader);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load crawler configuration", e);
    }
  }
}