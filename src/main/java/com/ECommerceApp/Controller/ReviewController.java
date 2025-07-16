package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Review;
import com.ECommerceApp.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/postReview")
    public Review postReview(@RequestBody Review review){ // user
        return reviewService.addReview(review);
    }

    @GetMapping("/getProductReview/{id}")
    public List<Review> getProductReview(@PathVariable String id){ // every user
        return reviewService.getReviewByProductId(id);
    }

    @PutMapping("/updateReview")
    public Review updateReview(@RequestBody Review review){ // done by user
        return reviewService.updateReview(review);
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
