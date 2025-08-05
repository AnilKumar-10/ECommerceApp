package com.ECommerceApp.ServiceInterface.Product;

import com.ECommerceApp.DTO.User.ReviewCreationRequest;
import com.ECommerceApp.DTO.User.ReviewDeletion;
import com.ECommerceApp.Model.Product.Review;

import java.util.List;

public interface IReviewService {

    List<Review> getReviewsByProduct(String productId);

    Review getReviewById(String id);

    Review updateReview(ReviewCreationRequest updatedReview);

    String deleteReview(String id);

    Review addReview(ReviewCreationRequest review);

    public Review getReviewByUserId(String productId,String userId);

    List<Review> getReviewByProductId(String id);

    String deleteReviewByUserId(ReviewDeletion reviewDeletion);

    public List<Review> getAllUserReviews(String userId);

    double getAverageSellerProductRating(String sellerId);
}

