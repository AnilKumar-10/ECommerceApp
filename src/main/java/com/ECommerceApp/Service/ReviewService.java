package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.User.ReviewCreationRequest;
import com.ECommerceApp.Exceptions.Product.ReviewNotFountException;
import com.ECommerceApp.Exceptions.User.UnknowUserReviewException;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.Product.Review;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.Repository.ProductRepository;
import com.ECommerceApp.Repository.ReviewRepository;
import com.ECommerceApp.Repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review getReviewById(String id) {
        log.info("Getting the review with is: "+id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFountException("Review not found with ID: " + id));
    }

    public Review updateReview( Review updatedReview) {
        log.info("updating the review : "+updatedReview.getProductId());
        Review existing = getReviewById(updatedReview.getId());
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        existing.setUpdatedAt(new Date());
        return reviewRepository.save(existing);
    }

    public String deleteReview(String id) {
        log.warn("deleting the review: "+id);
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFountException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
        return "Review is deleted successfully";

    }

    public Review addReview(ReviewCreationRequest review) {
        log.info("adding the new review to the product: "+review.getProductId());
        Review review1 = new Review();
        BeanUtils.copyProperties(review,review1);
        review1.setCreatedAt(new Date());
        review1.setUpdatedAt(new Date());
        if(usersRepository.existsById(review.getUserId())){
            review1.setVerifiedBuyer(true);
        }
        else{
            throw new UnknowUserReviewException("User is not verified ");
        }
        Review saved = reviewRepository.save(review1);

        updateProductRating(review.getProductId()); //  updates average rating of that product.

        return saved;
    }

    private void updateProductRating(String productId) {
        log.info("updating the product ratings after every new rating added.");
        List<Review> reviews = reviewRepository.findByProductId(productId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        double roundedValue = Math.round(average * 10.0) / 10.0;
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setRating(roundedValue);
            productRepository.save(product);
            Users user = usersRepository.findById(product.getSellerId()).get();
            user.setRating(getAverageSellerProductRating(user.getId()));
            // this will update the seller's avg rating when new rating is added to their products.
            usersRepository.save(user);
        }
    }

    public List<Review> getReviewByProductId(String id){
        return reviewRepository.findByProductId(id);
    }

    public List<Review> findByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public String deleteReviewByUserId(String userId) {
        log.warn("Deleting the review by the user: "+userId);
        if(!reviewRepository.existsByUserId(userId)){
            throw new ReviewNotFountException("The user didn't posted any review yet.");
        }
        reviewRepository.deleteByUserId(userId);
        return "Review is deleted successfully";
    }

    public double getAverageSellerProductRating(String sellerId) {
        log.info("calculating the average rating of seller after every new rating is added to their products.");
        List<Product> products = productRepository.findBySellerId(sellerId);
        if (products.isEmpty()) {
            return 0.0;
        }
        double sum = products.stream()
                .filter(p -> p.getRating() != 0.0)
                .mapToDouble(Product::getRating)
                .sum();
        long count = products.stream()
                .filter(p -> p.getRating() != 0.0)
                .count();
        return count == 0 ? 0.0 : Math.round((sum/count) * 10.0) / 10.0;
    }


}
