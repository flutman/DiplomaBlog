package com.example.diploma.repository;

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

   @Query(
           value = "SELECT * FROM posts WHERE (ptime < NOW() AND is_active = 1 AND moderation_status = 'ACCEPTED')",
           nativeQuery = true
   )
   List<Post> getAllPosts();

   @Query("SELECT COUNT(p) FROM Post p")
   long getCountPosts();

   @Query(
      value = "SELECT * FROM posts WHERE (ptime < NOW() AND is_active=true AND moderation_status = 'ACCEPTED') ORDER BY ptime ASC",
      nativeQuery = true
   )
   Page<Post> findEarlyPosts(Pageable pageable);

   @Query (
      value = "SELECT * FROM posts WHERE (ptime < NOW() AND is_active=true AND moderation_status = 'ACCEPTED') ORDER BY ptime DESC",
      nativeQuery = true
   )
   Page<Post> findRecentPosts(Pageable pageable);

   @Query(
           value = "SELECT *, SUM(pv.value) AS pv_sum FROM `posts` p " +
                   "LEFT JOIN `post_votes` pv ON p.pid = pv.post_id " +
                   "WHERE p.ptime < NOW() AND `is_active`= 1 AND `moderation_status` = 'ACCEPTED' " +
                   "GROUP BY p.pid " +
                   "ORDER BY pv_sum DESC",
           nativeQuery = true
   )
   Page<Post> getBestPosts(Pageable pageable);


   @Query(
           value = "SELECT *, COUNT(pc.id) AS pc_count FROM `posts` p " +
                   "LEFT JOIN `post_comments` pc ON p.pid = pc.post_id " +
                   " WHERE (p.ptime < NOW() AND `is_active`= 1 AND `moderation_status` = 'ACCEPTED')" +
                   " GROUP BY p.pid" +
                   " ORDER BY pc_count DESC",
           nativeQuery = true
   )
   Page<Post> findPopularPosts(Pageable pageable);

   List<Post> findAllByOrderByPtime();

   @Query(
           value = "SELECT * FROM `posts` " +
                     "WHERE (" +
                        "ptime < NOW() AND is_active = 1 AND moderation_status = 'ACCEPTED' " +
                        "AND (ptext LIKE %:query% OR title LIKE %:query%)" +
                     ")",
           nativeQuery = true
   )
   Page<Post> findPostsByQuery(String query, Pageable pageable);

   @Query(
           value = "SELECT * FROM `posts` " +
                   "WHERE " +
                   "DATE(ptime) = DATE(:date) AND is_active = 1 AND moderation_status = 'ACCEPTED'",
           nativeQuery = true
   )
   Page<Post> findPostsByDate(Instant date, Pageable pageable);

   @Query("SELECT p FROM Post p LEFT JOIN p.tags pt WHERE pt.name = :tag AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED'" )
   Page<Post> findPostsByTag(@Param("tag") String tag, Pageable pageable);
}
