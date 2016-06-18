package cobone.controllers;

import javax.validation.Valid;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import cobone.model.Comment;
import cobone.repo.CommentRepo;
import cobone.repo.DealRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentController {

	private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	@Autowired
	private CommentRepo commentRepo;
	@Autowired
	private DealRepo dealRepo;
	@Autowired
	private ObjectMapper objectMapper;

	@Value("${captchaSecret}")
	private String captchaSecret;

	@RequestMapping(method = RequestMethod.GET, path = "/{path}")
	public ResponseEntity<Page<Comment>> getComments(@PathVariable("path") String path,
			@PageableDefault(size = 5) Pageable pageable) {
		log.info("getting comments for path {}", path);
		return ResponseEntity.ok(commentRepo.getByDealPathOrderByCreatedDesc(path, pageable));
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addComments(@Valid @RequestBody(required = true) Comment comment) {
		log.debug("saving comments: {}", comment);
		try {
			validateCaptcha(comment.getCaptcha());
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		if (comment.getDeal().getId() != null) {
			comment.setDeal(dealRepo.findOne(comment.getDeal().getId()));
		} else {
			dealRepo.save(comment.getDeal());
		}
		commentRepo.save(comment);
		return ResponseEntity.ok().build();
	}

	private void validateCaptcha(String captcha) {
		try {
			log.debug("secret: {}", captchaSecret);
			HttpClient httpClient = HttpClientBuilder.create().build();

			URIBuilder builder = new URIBuilder(RECAPTCHA_VERIFY_URL);
			builder.addParameter("secret", captchaSecret).addParameter("response", captcha);
			HttpPost post = new HttpPost(builder.build());

			HttpEntity entity = httpClient.execute(post).getEntity();
			String jsonString = EntityUtils.toString(entity);

			log.debug("google reCaptcha returned: {}", jsonString);

			boolean success = objectMapper.readTree(jsonString).path("success").asBoolean();
			if (!success) {
				throw new RuntimeException("invalid captcha");
			}
		} catch (Exception ex) {
			throw new RuntimeException("invalid captcha; " + ex.getMessage());
		}
	}
}
