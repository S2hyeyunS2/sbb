package com.mysite.spring.comment;

import com.mysite.spring.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findByAuthorId(Long uwerId);
    Page<Comment> findByAuthor(SiteUser siteUser, Pageable pageable);
}
