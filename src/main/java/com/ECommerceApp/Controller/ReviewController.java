package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.ReviewCreationRequest;
import com.ECommerceApp.Model.Review;
import com.ECommerceApp.Service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController { // buyyer

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/postReview")
    public ResponseEntity<?> postReview(@Valid @RequestBody ReviewCreationRequest review){
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping("/getProductReview/{id}")
    public List<Review> getProductReview(@PathVariable String id){ // every user
        return reviewService.getReviewByProductId(id);
    }

    @PutMapping("/updateReview")
    public ResponseEntity<?> updateReview(@Valid @RequestBody Review review){ // done by user
        return ResponseEntity.ok(reviewService.updateReview(review));
    }

    @DeleteMapping("/deleteUsersReview/{userId}")
    public String deleteUserPostedReview(@PathVariable String userId){ // done by the user
        return reviewService.deleteReviewByUserId(userId);
    }

    @DeleteMapping("/deleteReview/{reviewId}")
    public String deleteReview(@PathVariable String reviewId){ //admin,seller
        return reviewService.deleteReview(reviewId);
    }

}
