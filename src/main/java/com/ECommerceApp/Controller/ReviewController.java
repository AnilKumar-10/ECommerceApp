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
    public Review postReview(@RequestBody Review review){
        return reviewService.addReview(review);
    }

    @GetMapping("/getReview/{id}")
    public List<Review> getProductReview(@PathVariable String id){
        return reviewService.getReviewByProductId(id);
    }

}
