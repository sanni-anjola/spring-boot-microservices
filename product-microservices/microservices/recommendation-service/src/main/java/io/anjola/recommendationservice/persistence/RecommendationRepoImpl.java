package io.anjola.recommendationservice.persistence;

import org.reactivestreams.Publisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Repository
public class RecommendationRepoImpl implements RecommendationRepository{

    private final static String KEY = "RECOMMENDATION";

    private final ReactiveRedisOperations<String, RecommendationEntity> redisOperations;
    private final ReactiveHashOperations<String, String, RecommendationEntity> hashOperations;

    public RecommendationRepoImpl(ReactiveRedisOperations<String, RecommendationEntity> redisOperations) {
        this.redisOperations = redisOperations;
        this.hashOperations = redisOperations.opsForHash();
    }

    @Override
    public Flux<RecommendationEntity> findByProductId(int productId) {
        return hashOperations.values(KEY)
                .filter(r -> r.getProductId() == productId)
                .sort(Comparator.comparingInt(RecommendationEntity::getProductId));
    }

    @Override
    public Flux<RecommendationEntity> findByRecommendationId(int recommendationId) {
        return hashOperations.values(KEY)
                .filter(r -> r.getRecommendationId() == recommendationId)
                .sort(Comparator.comparingInt(RecommendationEntity::getRecommendationId));
    }

    @Override
    public Mono<RecommendationEntity> findByProductIdAndRecommendationId(int productId, int recommendationId) {
        return hashOperations.values(KEY)
                .filter(r -> r.getProductId() == productId && r.getRecommendationId() == recommendationId)
                .singleOrEmpty();
    }

    @Override
    public Mono<Boolean> existsByProductIdAndRecommendationId(int productId, int recommendationId) {
        return findByProductIdAndRecommendationId(productId, recommendationId).hasElement();
    }
    @Override
    public Mono<Void> deleteByProductId(int productId) {
        return hashOperations.remove(KEY, productId).then();
    }

    @Override
    public Mono<RecommendationEntity> findById(String id) {
        return hashOperations.get(KEY, id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return hashOperations.hasKey(KEY, id);
    }

    @Override
    public Flux<RecommendationEntity> findAll() {
        return hashOperations.values(KEY);
    }

    @Override
    public Mono<Long> count() {
        return hashOperations.values(KEY).count();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return hashOperations.remove(KEY, id).then();
    }

    @Override
    public Mono<Void> delete(RecommendationEntity entity) {
        return hashOperations.remove(KEY, entity.getId()).then();
    }

    @Override
    public Mono<Void> deleteAll() {
        return hashOperations.delete(KEY).then();
    }

    @Override
    public Mono<RecommendationEntity> save(RecommendationEntity entity) {

        if (entity.getId() == null) {

            /*
                Indexed Unique on productId & recommendationId implementation.
                Something like this in:

                MySQL entity class:
                @Table(name = "recommendation", indexes = {@Index(name = "rec_unique_idx", unique = true,
                        columnList = "productId,recommendationId" )})

                MongoDB Document class:
                @Document(collection="recommendations")
                @CompoundIndex(name = "prod-rec-id", unique = true, def = "{'productId': 1, 'recommendationId' : 1}")
             */
            entity.setId(createEntityId());
            entity.setVersion(0);
            return Mono.defer(() -> addOrUpdateEntity(entity, existsByProductIdAndRecommendationId(entity.getProductId(), entity.getRecommendationId())));

//            if (findByProductIdAndRecommendationId(entity.getProductId(), entity.getRecommendationId()).isPresent())
//                throw new DuplicateKeyException("Duplicate key, Product Id: " +  entity.getProductId() +
//                        ", Recommendation Id: " + entity.getRecommendationId());
//
//            entity.setId(createEntityId());
//            entity.setVersion(0);
//        } else {
//            Optional<RecommendationEntity> foundEntity = findById(entity.getId());
//            if (foundEntity.isEmpty()) {
//                entity.setId(createEntityId());
//                entity.setVersion(0);
//            } else {
//                int version = foundEntity.get().getVersion();
//                if (version == entity.getVersion())
//                    entity.setVersion(version + 1);
//                else
//                    throw new OptimisticLockingFailureException("This data has been updated earlier by another object.");
//            }
//        }
//
//        hashOperations.put(KEY, entity.getId(), entity);
//            return entity;
        }else {
            return findById(entity.getId())
                    .flatMap(r -> {
                        if(!Objects.equals(r.getVersion(), entity.getVersion())){
                            return Mono.error(
                                  new OptimisticLockingFailureException("This data has been updated earlier by another object.")
                            );
                        } else {
                            entity.setVersion(entity.getVersion() + 1);

                        }
                        return addOrUpdateEntity(entity, Mono.just(true));
                    });


        }
    }

    public String createEntityId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public <S extends RecommendationEntity> Flux<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends RecommendationEntity> Flux<S> saveAll(Publisher<S> entityStream) {
        return null;
    }

    @Override
    public Mono<RecommendationEntity> findById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Publisher<String> id) {
        return null;
    }

    @Override
    public Flux<RecommendationEntity> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public Flux<RecommendationEntity> findAllById(Publisher<String> idStream) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<? extends String> strings) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends RecommendationEntity> entities) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends RecommendationEntity> entityStream) {
        return null;
    }

    private Mono<RecommendationEntity> addOrUpdateEntity(RecommendationEntity entity, Mono<Boolean> exists) {
        return exists.flatMap(exist -> {
                    if(exist) {
                        return Mono.error(new DuplicateKeyException("Duplicate key, Product Id: " + entity.getProductId() +
                                ", Recommendation Id: " + entity.getRecommendationId()));
                    } else {
                        return hashOperations.put(KEY, entity.getId(), entity)
                                .map(isSaved -> entity);
                    }
                })
                .thenReturn(entity);
    }

}
