package io.anjola.recommendationservice.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RecommendationRepository extends ReactiveCrudRepository<RecommendationEntity, String> {

    Flux<RecommendationEntity> findByProductId(int productId);
    Flux<RecommendationEntity> findByRecommendationId(int recommendationId);
    Mono<RecommendationEntity> findByProductIdAndRecommendationId(int productId, int recommendationId);
    Mono<Boolean> existsByProductIdAndRecommendationId(int productId, int recommendationId);

    Mono<Void> deleteByProductId(int productId);

    Mono<RecommendationEntity> save(RecommendationEntity entity);
}
