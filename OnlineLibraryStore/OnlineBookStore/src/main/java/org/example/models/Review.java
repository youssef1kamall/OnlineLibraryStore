package org.example.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Review {
    private int id;
    private int bookId;
    private int customerId;
    private String reviewText;
    private int rating;

    private static final String REVIEW_FILE_PATH = "src/main/resources/reviews.txt"; // Path to the review text file

    // Default constructor
    public Review() {}

    // Constructor with parameters
    public Review(int bookId, int customerId, String reviewText, int rating) {
        this.bookId = bookId;
        this.customerId = customerId;
        this.reviewText = reviewText;
        setRating(rating); // Set rating using the setter to ensure it's valid
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    // Method to update review text and rating
    public void updateReview(String newReviewText, int newRating) {
        this.reviewText = newReviewText;
        setRating(newRating); // Ensure the new rating is valid
    }

    @Override
    public String toString() {
        return id + "|" + bookId + "|" + customerId + "|" + reviewText + "|" + rating;
    }

    // Read reviews from the file
    private static List<Review> readReviewsFromFile() {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REVIEW_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    int bookId = Integer.parseInt(parts[1]);
                    int customerId = Integer.parseInt(parts[2]);
                    String reviewText = parts[3];
                    int rating = Integer.parseInt(parts[4]);

                    Review review = new Review(bookId, customerId, reviewText, rating);
                    review.setId(id);
                    reviews.add(review);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Write reviews to the file
    private static void writeReviewsToFile(List<Review> reviews) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REVIEW_FILE_PATH))) {
            for (Review review : reviews) {
                writer.write(review.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get reviews by bookId from file
    public static List<Review> getReviewsByBookId(int bookId) {
        List<Review> reviewsForBook = new ArrayList<>();
        List<Review> reviews = readReviewsFromFile();
        for (Review review : reviews) {
            if (review.getBookId() == bookId) {
                reviewsForBook.add(review);
            }
        }
        return reviewsForBook;
    }

    // Add a review and save it to the file
    public static void addReview(Review review) {
        List<Review> reviews = readReviewsFromFile();

        // Generate a new ID for the review based on the current list size
        int newId = reviews.isEmpty() ? 1 : reviews.get(reviews.size() - 1).getId() + 1;
        review.setId(newId);

        // Add the new review to the list
        reviews.add(review);

        // Save updated list of reviews back to the file
        writeReviewsToFile(reviews);
    }

    // Get reviews by customerId from file
    public static List<Review> viewReviewsByCustomerId(int customerId) {
        List<Review> customerReviews = new ArrayList<>();
        List<Review> reviews = readReviewsFromFile();
        for (Review review : reviews) {
            if (review.getCustomerId() == customerId) {
                customerReviews.add(review);
            }
        }
        return customerReviews;
    }

    // Update an existing review by id
    public static boolean updateReview(int id, String newReviewText, int newRating) {
        List<Review> reviews = readReviewsFromFile();
        for (Review review : reviews) {
            if (review.getId() == id) {
                review.updateReview(newReviewText, newRating);
                writeReviewsToFile(reviews); // Save updated reviews to the file
                return true; // Review updated successfully
            }
        }
        return false; // Review not found
    }
}
