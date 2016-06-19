package cobone.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cobone.model.Info;

@RestController
@RequestMapping("/")
public class IndexController {

	@Value("${app_version}")
	private String appVersion;

	@RequestMapping(method = RequestMethod.GET, path = "/info")
	public ResponseEntity<?> getVersion() {
		return ResponseEntity.ok(new Info(appVersion));
	}
}
