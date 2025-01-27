package com.udacity.webcrawler.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Data class representing the final result of a web crawl.
 */
public final class CrawlResult {
  private final Map<String, Integer> wordCounts;
  private final int urlsVisited;

  /**
   * Constructs a {@link CrawlResult} with the given word counts and visited URL count.
   */
  private CrawlResult(Map<String, Integer> wordCounts, int urlsVisited) {
    this.wordCounts = wordCounts;
    this.urlsVisited = urlsVisited;
  }

  @JsonProperty("wordCounts")
  public Map<String, Integer> getWordCounts() {
    return wordCounts;
  }

  @JsonProperty("urlsVisited")
  public int getUrlsVisited() {
    return urlsVisited;
  }

  public static final class Builder {
    private Map<String, Integer> wordFrequencies = new LinkedHashMap<>();
    private int pageCount;

    @JsonProperty("wordCounts")
    public Builder setWordCounts(Map<String, Integer> wordCounts) {
      this.wordFrequencies = new LinkedHashMap<>(Objects.requireNonNull(wordCounts));
      return this;
    }

    @JsonProperty("urlsVisited")
    public Builder setUrlsVisited(int pageCount) {
      this.pageCount = pageCount;
      return this;
    }

    public CrawlResult build() {
      return new CrawlResult(Collections.unmodifiableMap(wordFrequencies), pageCount);
    }
  }
}