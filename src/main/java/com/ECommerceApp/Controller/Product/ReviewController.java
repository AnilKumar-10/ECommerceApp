package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.DTO.User.ReviewCreationRequest;
import com.ECommerceApp.DTO.User.ReviewDeletion;
import com.ECommerceApp.Model.Product.Review;
import com.ECommerceApp.ServiceInterface.Product.IReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController { // buyer

    @Autowired
    private IReviewService reviewService;
    //  BUYER: Post a new review
    @PreAuthorize("hasPermission('REVIEW', 'INSERT')")
    @PostMapping("/postReview")
    public ResponseEntity<?> postReview(@Valid @RequestBody ReviewCreationRequest review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    //  ALL ROLES: View product reviews
    @PreAuthorize("hasPermission('REVIEW', 'READ')")
    @GetMapping("/getProductReview/{productId}")
    public List<Review> getProductReview(@PathVariable String productId) {
        return reviewService.getReviewByProductId(productId);
    }

    //  BUYER/ADMIN: Update a review (buyer for own, admin for any)
    @PreAuthorize("hasPermission('REVIEW', 'UPDATE')")
    @PutMapping("/updateReview")
    public ResponseEntity<?> updateReview(@Valid @RequestBody ReviewCreationRequest review) {
        return ResponseEntity.ok(reviewService.updateReview(review));
    }

    //  BUYER: Delete own review
    @PreAuthorize("hasPermission('REVIEW', 'DELETE')")
    @DeleteMapping("/deleteUsersReview")
    public ResponseEntity<?> deleteUserPostedReview(@RequestBody ReviewDeletion reviewDeletion) {
        return ResponseEntity.ok(reviewService.deleteReviewByUserId(reviewDeletion));
    }

    //  ADMIN: Delete any review
    @PreAuthorize("hasPermission('REVIEW', 'DELETE')")
    @DeleteMapping("/deleteReview/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }

    //  ALL: View all reviews by a specific user
    @PreAuthorize("hasPermission('REVIEW', 'READ')")
        @GetMapping("/getAllUserReviews/{userID}")
    public ResponseEntity<?> getAllReviewsByUser(@PathVariable String userID) {
        List<Review> reviews = reviewService.getAllUserReviews(userID);
        if(!reviews.isEmpty()){
            return ResponseEntity.ok(reviews);
        }
        return ResponseEntity.ok("The user haven't posted review");
    }


}
