package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.ReviewNotFountException;
import com.ECommerceApp.Exceptions.UnknowUserReviewException;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Model.Review;
import com.ECommerceApp.Repository.ProductRepository;
import com.ECommerceApp.Repository.ReviewRepository;
import com.ECommerceApp.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFountException("Review not found with ID: " + id));
    }

    public Review updateReview( Review updatedReview) {
        Review existing = getReviewById(updatedReview.getId());
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        existing.setUpdatedAt(new Date());
        return reviewRepository.save(existing);
    }

    public String deleteReview(String id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFountException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
        return "Review is deleted successfully";

    }

    public Review addReview(Review review) {
        review.setCreatedAt(new Date());
        review.setUpdatedAt(new Date());
        if(usersRepository.existsById(review.getUserId())){
            review.setVerifiedBuyer(true);
        }
        else{
            throw new UnknowUserReviewException("User is not verified ");
        }
        Review saved = reviewRepository.save(review);

        updateProductRating(review.getProductId()); // ← update average

        return saved;
    }

    private void updateProductRating(String productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        double roundedValue = Math.round(average * 10.0) / 10.0;
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setRating(roundedValue);
            productRepository.save(product);
        }
    }

    public List<Review> getReviewByProductId(String id){
        return reviewRepository.findByProductId(id);
    }

    public List<Review> findByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public String deleteReviewByUserId(String userId) {
        if(!reviewRepository.existsByUserId(userId)){
            throw new ReviewNotFountException("The user didn't posted any review yet.");
        }
        reviewRepository.deleteByUserId(userId);
        return "Review is deleted successfully";
    }
}
