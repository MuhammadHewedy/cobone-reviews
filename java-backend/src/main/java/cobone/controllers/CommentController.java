package cobone.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cobone.model.Comment;
import cobone.repo.CommentRepo;
import cobone.repo.DealRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	private CommentRepo commentRepo;
	@Autowired
	private DealRepo dealRepo;

	@RequestMapping(method = RequestMethod.GET, path = "/{path}")
	public ResponseEntity<List<Comment>> getComments(@PathVariable("path") String path) {
		log.info("getting comments for path {}", path);
		return ResponseEntity.ok(commentRepo.getByDealPathOrderByCreatedDesc(path));
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addComments(@Valid @RequestBody(required = true) Comment comment) {
		log.info("saving comments: ", comment);

		if (comment.getDeal().getId() != null) {
			comment.setDeal(dealRepo.findOne(comment.getDeal().getId()));
		} else {
			dealRepo.save(comment.getDeal());
		}
		commentRepo.save(comment);
		return ResponseEntity.ok().build();
	}

}
