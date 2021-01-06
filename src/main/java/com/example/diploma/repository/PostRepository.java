package com.example.diploma.repository;

import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PostRepository  extends CrudRepository<Post,Integer> {

   @Query("SELECT COUNT(p) FROM Post p")
   long getCountPosts();

   @Query("SELECT p FROM Post p WHERE p.time < NOW() AND isActive = 1 AND moderationStatus = 'ACCEPTED' ORDER BY time ASC")
   Page<Post> findEarlyPosts(Pageable pageable);

   @Query("SELECT p FROM Post p WHERE p.time < NOW() AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' ORDER BY time DESC")
   Page<Post> findRecentPosts(Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN p.postVotes pv WHERE p.time < NOW() AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' GROUP BY p.id ORDER BY SUM(pv.value) DESC")
   Page<Post> getBestPosts(Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN p.comments pc WHERE p.time < NOW() AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' GROUP BY p.id ORDER BY COUNT(pc.id) DESC")
   Page<Post> getPopularPosts(Pageable pageable);

   @Query("SELECT p FROM Post p WHERE isActive = 1 AND moderationStatus = 'ACCEPTED' ORDER BY time")
   List<Post> getPostsForCalendar();

   @Query("SELECT p FROM Post p WHERE time < NOW() AND isActive = 1 AND moderationStatus = 'ACCEPTED' AND (text LIKE %:query% OR title LIKE %:query%)")
   Page<Post> findPostsByQuery(@Param("query") String query, Pageable pageable);

   @Query("SELECT p FROM Post p WHERE DATE(time) = DATE(:date) AND isActive = 1 AND moderationStatus = 'ACCEPTED'")
   Page<Post> findPostsByDate(Instant date, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN p.tags pt WHERE pt.name = :tag AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED'" )
   Page<Post> findPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (p.isActive = :active AND p.moderationStatus = :status AND p.user.id = :id)")
    Page<Post> findMyPosts(int id, ModerationStatus status, boolean active, Pageable pageable);
}
