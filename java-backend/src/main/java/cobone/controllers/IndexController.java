package cobone.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cobone.model.helper.Info;

@RestController
@RequestMapping("/")
public class IndexController {

	@Value("${app_version}")
	private String appVersion;

	@RequestMapping(method = RequestMethod.GET, path = "/info")
	public ResponseEntity<?> getVersion() {
		return ResponseEntity.ok(new Info(appVersion));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/ar")
	public void arabic(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.getRequestDispatcher("/index_ar.html").forward(req, resp);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/stats")
	public void stats(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.getRequestDispatcher("/statistics.html").forward(req, resp);
	}
}
