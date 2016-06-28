package cobone.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cobone.model.ActionLog;
import cobone.repo.ActionLogRepo;

@RestController
@RequestMapping("/logger")
public class LoggerController {

	@Autowired
	private ActionLogRepo actionLogRepo;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> logUIActions(@RequestBody ActionLog actionLog) {
		actionLogRepo.save(actionLog);
		return ResponseEntity.ok().build();
	}

}
