package io.anjola.api.composite.service;

public class ServiceAddress {

    private final int recommendationId;
    private final String author;
    private final int rate;

    public ServiceAddress(int recommendationId, String author, int rate) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRate() {
        return rate;
    }
}
