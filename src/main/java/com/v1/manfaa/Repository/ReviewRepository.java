package com.v1.manfaa.Repository;

import com.v1.manfaa.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review , Integer> {
    Review findReviewById(Integer id);
}
