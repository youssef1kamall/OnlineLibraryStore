package org.example.services;

import org.example.models.Review;

import java.util.List;

public class ReviewService {


    public List<Review> getReviewsForBook(int bookId) {
        return Review.getReviewsByBookId(bookId);
    }

    public void addReview(int bookId, int customerId, String reviewText, int rating) {
        // Validate the inputs
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review(bookId, customerId, reviewText, rating);

        // Add the review to the review database
        Review.addReview(review);
    }
}