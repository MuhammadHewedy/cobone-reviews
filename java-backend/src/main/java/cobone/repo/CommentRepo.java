package cobone.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cobone.model.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long> {

	public List<Comment> getByDealPathOrderByCreatedDesc(String path);
}
