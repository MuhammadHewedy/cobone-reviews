package cobone.controllers.api;

import static java.util.stream.Collectors.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cobone.controllers.util.LoggerCHelper;
import cobone.model.ActionLog;
import cobone.model.ActionLog.Action;
import cobone.model.helper.DailyCount;
import cobone.model.helper.Series;
import cobone.repo.ActionLogRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/logger")
public class LoggerController {

	@Autowired
	private ActionLogRepo actionLogRepo;
	@Autowired
	private LoggerCHelper helper;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> logUIActions(@RequestBody ActionLog actionLog) {
		log.debug("logging {} action", actionLog.getAction());
		actionLogRepo.save(actionLog);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/stats/all")
	public ResponseEntity<?> getAllActions() {

		List<DailyCount> list = actionLogRepo.getAllActionsNoTimeSpent();

		Function<List<Series>, List<Series>> seriesMapper = series -> series.stream()
				// map to Entry of Action to List<Long> just for caching purpose
				.map(s -> new SimpleEntry<Action, List<Long>>(Action.forName(s.getName()), s.getData()))
				// filter out instances with order <= 0
				.filter(ts -> ts.getKey().getOrder() > 0)
				// sort based on the order
				.sorted(Comparator.comparing(ts -> ts.getKey().getOrder()))
				// get displayName property instead of name() build-in property
				.map(ts -> new Series(ts.getKey().getDisplayName(), ts.getValue()))
				// collect back
				.collect(toList());

		return ResponseEntity.ok(helper.convertToChartsFormat(list, EnumSet.allOf(Action.class), seriesMapper));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/stats/referrer/{action}")
	public ResponseEntity<?> getReferrersForAction(@PathVariable("action") Action action) {
		List<DailyCount> list;
		if (action == Action.TIME_SPENT) {
			list = actionLogRepo.getReferrerAvgByAction(action);
		} else {
			list = actionLogRepo.getReferrerCountByAction(action);
		}

		return ResponseEntity.ok(helper.convertToChartsFormat(list, actionLogRepo.findDistinctReferrer(action), null));
	}

}
