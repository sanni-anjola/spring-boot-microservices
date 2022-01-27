package io.anjola.productservice.core.product.controller;

import io.anjola.api.core.product.Product;
import io.anjola.api.core.product.ProductService;
import io.anjola.util.exceptions.InvalidInputException;
import io.anjola.util.exceptions.NotFoundException;
import io.anjola.util.exceptions.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductService {


    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);
    private final ServiceUtil serviceUtil;

    public ProductController(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProduct(int productId) {

        LOG.debug("/product return the found product for productId={}", productId);
        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
//        if (productId == 0) throw new BadRequestException("Bad productId: " + productId);
        //For testing sake: Let's assume productId 13 does not exist
        if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);

        return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());

    }
}
