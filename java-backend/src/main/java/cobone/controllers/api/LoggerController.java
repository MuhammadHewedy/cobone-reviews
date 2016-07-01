package cobone.controllers.api;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.InternetDomainName;

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
	private ObjectMapper objectMapper;

	@Value("${group-by-topPrivateDomain:false}")
	private Boolean groupByTPDomain;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> logUIActions(@RequestBody ActionLog actionLog) {
		log.debug("logging {} action", actionLog.getAction());
		actionLogRepo.save(actionLog);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/stats/all")
	public ResponseEntity<?> getAllActions() {

		List<DailyCount> list = actionLogRepo.getAllActionsNoTimeSpent();
		List<DailyCount> timeSpentList = actionLogRepo.getTimeSpentAction().stream().map(
				arr -> new DailyCount((Date) arr[0], (Object) Action.forName(Action.TIME_SPENT.name()), (Long) arr[1]))
				.collect(toList());
		list.addAll(timeSpentList);

		return ResponseEntity.ok(optimizeForCharts(list, EnumSet.allOf(Action.class),
				(List<Series> series) -> series.stream().filter(s -> Action.forName(s.getName()).getOrder() > 0)
						.sorted(Comparator.comparing(s -> Action.forName(s.getName()).getOrder()))
						.map(s -> new Series(Action.forName(s.getName()).getName(), s.getData())).collect(toList())));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/stats/referrer/{action}")
	public ResponseEntity<?> getAllActivites(@PathVariable("action") Action action) {
		List<DailyCount> list;
		if (action == Action.TIME_SPENT) {
			list = actionLogRepo.getReferrerAvgByAction(action);
		} else {
			list = actionLogRepo.getReferrerCountByAction(action);
		}

		return ResponseEntity.ok(optimizeForCharts(list, actionLogRepo.findDistinctReferrer(action), null));
	}

	/// ------------------------------ private --------------------------------

	private void zeroFill(Entry<Date, List<DailyCount>> entry, Collection<?> allList) {
		List<Object> entryList = entry.getValue().stream().map(ev -> ev.getAction()).collect(Collectors.toList());
		allList.stream().filter(a -> !entryList.contains(a))
				.forEach(a -> entry.getValue().add(new DailyCount(entry.getKey(), a, 0l)));
	}

	private Object optimizeForCharts(List<DailyCount> list, Collection<?> allList,
			Function<List<Series>, List<Series>> mapper) {
		// Group by Day
		Map<Date, List<DailyCount>> collect = list.stream().collect(groupingBy(DailyCount::getDay));
		// Fill missing data by Zeros
		collect.entrySet().stream().forEach(e -> zeroFill(e, allList));
		// Sort by Day
		collect = new TreeMap<>(collect);

		// Group values by Action and get a Map of Action to List of Counts
		Map<Object, List<Long>> collect2 = collect.values().stream().flatMap(o -> o.stream())
				.collect(groupingBy(DailyCount::getAction, mapping(DailyCount::getValue, toList())));

		// Group values by the top private domain
		Map<Object, List<Long>> collect3 = collect2;
		if (groupByTPDomain) {
			collect3 = collect2.entrySet().stream().collect(groupingBy(e -> getDomainName(e.getKey()),
					reducing(new ArrayList<Long>(), e -> e.getValue(), (l1, l2) -> addTwoLists(l1, l2))));
		}

		// Convert the Action-List of Counts Map to List of Series
		List<Series> series = collect3.entrySet().stream().map(e -> new Series(e.getKey().toString(), e.getValue()))
				.collect(Collectors.toList());

		// If Series Mapper is not null, Apply user-provided mapping
		if (mapper != null) {
			series = mapper.apply(series);
		}

		ObjectNode root = objectMapper.createObjectNode();
		root.putPOJO("categories", collect.keySet());
		root.putPOJO("series", series);

		return root;
	}

	private List<Long> addTwoLists(List<Long> acc, List<Long> list) {
		if (acc.isEmpty()) {
			return list;
		} else {
			for (int i = 0; i < acc.size(); i++) {
				acc.set(i, acc.get(i) + list.get(i));
			}
			return acc;
		}
	}

	// Example: www.google.com.sa, google.com, google.eg all translated to
	// google
	private String getDomainName(Object referrer) {
		try {
			String topPrivateDomain = InternetDomainName.from(referrer.toString()).topPrivateDomain().name();
			String publicSuffix = InternetDomainName.from(referrer.toString()).publicSuffix().name();
			String onlyDomainName = topPrivateDomain.replace(publicSuffix, "");
			return onlyDomainName.endsWith(".") ? onlyDomainName.substring(0, onlyDomainName.length() - 1)
					: onlyDomainName;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return referrer.toString();
		}
	}
}
