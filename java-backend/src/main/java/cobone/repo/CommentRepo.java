package cobone.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cobone.model.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long> {

	public Page<Comment> getByDealPathOrderByCreatedDesc(String path, Pageable pageable);
}
