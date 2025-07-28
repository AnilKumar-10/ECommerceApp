package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.DTO.User.ReviewCreationRequest;
import com.ECommerceApp.DTO.User.ReviewDeletion;
import com.ECommerceApp.Model.Product.Review;
import com.ECommerceApp.ServiceInterface.IReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController { // buyyer

    @Autowired
    private IReviewService reviewService;

    @PostMapping("/postReview")
    public ResponseEntity<?> postReview(@Valid @RequestBody ReviewCreationRequest review){
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping("/getProductReview/{id}")
    public List<Review> getProductReview(@PathVariable String id){ // every user
        return reviewService.getReviewByProductId(id);
    }

    @PutMapping("/updateReview")
    public ResponseEntity<?> updateReview(@Valid @RequestBody ReviewCreationRequest review){ // done by user
        return ResponseEntity.ok(reviewService.updateReview(review));
    }

    @DeleteMapping("/deleteUsersReview")
    public String deleteUserPostedReview(@RequestBody ReviewDeletion reviewDeletion){ // done by the user
        return reviewService.deleteReviewByUserId(reviewDeletion);
    }

    @DeleteMapping("/deleteReview/{reviewId}")
    public String deleteReview(@PathVariable String reviewId){ //admin,seller
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/getAllUserReviews/{userID}")
    public List<Review> getAllReviewsByUser(@PathVariable String userID){
        return reviewService.getAllUserReviews(userID);
    }


}
