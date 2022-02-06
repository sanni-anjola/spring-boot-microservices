package io.anjola.recommendationservice.controller;

import io.anjola.recommendationservice.persistence.RecommendationEntity;
import io.anjola.recommendationservice.persistence.RecommendationRepoImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RecommendationRepoService {
    private final RecommendationRepoImpl repository;

    public RecommendationRepoService(RecommendationRepoImpl repository) {
        this.repository = repository;
    }

    Mono<RecommendationEntity> save(RecommendationEntity recommendation) {
        return repository.save(recommendation);
    }

    Mono<RecommendationEntity> findById(String id){
        return repository.findById(id);
    }

    public Flux<RecommendationEntity> findByProductId(int productId) {
        return repository.findByProductId(productId);
    }

    public Mono<Void> deleteByProductId(int productId) {
        return repository.deleteByProductId(productId);
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    public Mono<Void> delete(RecommendationEntity entity) {
        return repository.delete(entity);
    }

    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    public Mono<Long> count() {
        return repository.count();
    }

    public Mono<Boolean> existsById(String id) {
        return repository.existsById(id);
    }
}
