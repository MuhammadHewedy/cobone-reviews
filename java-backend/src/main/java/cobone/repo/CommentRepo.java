package cobone.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import cobone.model.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long> {

	Page<Comment> getByDealPathAndActiveIsTrueOrderByCreatedDesc(String path, Pageable pageable);

	@Modifying
	@Transactional
	@Query("update Comment set active = false where id = :id and uuid = :uuid")
	Integer deActivate(@Param("id") Long id, @Param("uuid") String uuid);

}
