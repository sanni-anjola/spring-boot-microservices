package io.anjola.productcompositeservice.controller;

import io.anjola.api.composite.ProductAggregate;
import io.anjola.api.composite.ProductCompositeService;
import io.anjola.api.composite.service.RecommendationSummary;
import io.anjola.api.composite.service.ReviewSummary;
import io.anjola.api.composite.service.ServiceAddress;
import io.anjola.api.core.product.Product;
import io.anjola.api.core.recommendation.Recommendation;
import io.anjola.api.core.review.Review;
import io.anjola.productcompositeservice.integration.ProductCompositeIntegration;
import io.anjola.util.exceptions.NotFoundException;
import io.anjola.util.exceptions.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeController implements ProductCompositeService {

    private final ServiceUtil serviceUtil;
    private ProductCompositeIntegration integration;

    @Autowired
    public ProductCompositeController(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = integration.getProduct(productId);

        if(product == null) throw new NotFoundException("No product found for productId: " + productId);

        List<Recommendation> recommendations = integration.getRecommendations(productId);
        List<Review> reviews = integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations,
                                                    List<Review> reviews, String serviceAddress) {

        //1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        //2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(recommendation -> new RecommendationSummary(recommendation.getRecommendationId(),
                                recommendation.getAuthor(), recommendation.getRate()))
                        .collect(Collectors.toList());

        //3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream()
                        .map(review -> new ReviewSummary(review.getReviewId(), review.getAuthor(), review.getSubject()))
                        .collect(Collectors.toList());

        //4. Create info regarding the involved microservices addresses
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ?
                recommendations.get(0).getServiceAddress() : "";
        ServiceAddress serviceAddresses = new ServiceAddress(serviceAddress, productAddress,
                reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}
