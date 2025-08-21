package com.ECommerceApp.ServiceImplementation.Product;

import com.ECommerceApp.DTO.User.ReviewCreationRequest;
import com.ECommerceApp.DTO.User.ReviewDeletion;
import com.ECommerceApp.Exceptions.Product.ReviewAlreadyExistsException;
import com.ECommerceApp.Exceptions.Product.ReviewNotFountException;
import com.ECommerceApp.Exceptions.User.UnknowUserReviewException;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.Product.Review;
import com.ECommerceApp.Model.User.Users;
import com.ECommerceApp.Repository.Product.ReviewRepository;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.Product.IReviewService;
import com.ECommerceApp.ServiceInterface.User.UserServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class ReviewService implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserServiceInterface userService;
    @Autowired
    private IProductService productService;

    public Review addReview(ReviewCreationRequest review) {
        log.info("adding the new review to the product: {}", review.getProductId());
        if(reviewRepository.existsByUserId(review.getUserId())){
            throw new ReviewAlreadyExistsException("User have already posted the review");
        }
        Review review1 = new Review();
        BeanUtils.copyProperties(review,review1);
        review1.setCreatedAt(new Date());
        review1.setUpdatedAt(new Date());
        if(userService.getUserById(review.getUserId())!=null){
            review1.setVerifiedBuyer(true);
        }else{
            throw new UnknowUserReviewException("User is not verified ");
        }
        Review saved = reviewRepository.save(review1);
        updateProductRating(review.getProductId()); //  updates average rating of that product.
        return saved;
    }


    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }


    public Review getReviewById(String id) {
        log.info("Getting the review with is: {}", id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFountException("Review not found with ID: " + id));
    }


    public Review updateReview( ReviewCreationRequest updatedReview) {
        log.info("updating the review : {}", updatedReview.getProductId());
        Review existing = getReviewByUserId(updatedReview.getProductId(), updatedReview.getUserId());
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        existing.setUpdatedAt(new Date());
        Review review = reviewRepository.save(existing);
        updateProductRating(review.getProductId()); //  updates average rating of that product.
        return review;
    }


    public Review getReviewByUserId(String productId,String userId) {
        return reviewRepository.findByProductIdAndUserId(productId,userId).orElseThrow(()->new ReviewNotFountException("There is no review with these ids"));
    }


    public String deleteReview(String id) {
        log.warn("deleting the review: {}", id);
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFountException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
        return "Review is deleted successfully";

    }


    private void updateProductRating(String productId) {
        log.info("updating the product ratings after every new rating added.");
        List<Review> reviews = reviewRepository.findByProductId(productId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        double roundedValue = Math.round(average * 10.0) / 10.0;
        Product product = productService.getProductById(productId);
        if (product != null) {
            product.setRating(roundedValue);
            productService.saveProduct(product);
            Users user = userService.getUserById(product.getSellerId());
            user.setRating(getAverageSellerProductRating(user.getId()));
            // this will update the seller's avg rating when new rating is added to their products.
            userService.saveUser(user);
        }
    }


    public List<Review> getReviewByProductId(String id){
        return reviewRepository.findByProductId(id);
    }


    public String deleteReviewByUserId(ReviewDeletion reviewDeletion) {
        log.warn("Deleting the review by the user: {}", reviewDeletion.getUserId());
        if(!reviewRepository.existsByUserId(reviewDeletion.getUserId())){
            throw new ReviewNotFountException("The user didn't posted any review yet.");
        }
        reviewRepository.deleteByUserIdAndProductId(reviewDeletion.getUserId(), reviewDeletion.getProductId());
        updateProductRating(reviewDeletion.getProductId());
        return "Review is deleted successfully";
    }


    public double getAverageSellerProductRating(String sellerId) {
        log.info("calculating the average rating of seller after every new rating is added to their products.");
        List<Product> products = productService.getProductsBySellerId(sellerId);
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


    public List<Review> getAllUserReviews(String userId){
        return reviewRepository.findByUserId(userId);
    }


}
