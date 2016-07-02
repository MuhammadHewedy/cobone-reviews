package cobone.controllers.util;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;
import com.google.common.net.InternetDomainName;

import cobone.model.helper.DailyCount;
import cobone.model.helper.Series;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggerCHelper { // LoggerControllerHelper

	@Autowired
	private ObjectMapper objectMapper;
	@Value("${group-by-topPrivateDomain:false}")
	private Boolean groupByTPDomain;

	public Object convertToChartsFormat(List<DailyCount> list, Collection<?> allDailyNames,
			Function<List<Series>, List<Series>> mapper) {
		// Group by Day, and return sorted by Date
		Map<Date, List<DailyCount>> collect = new TreeMap<>(list.stream().collect(groupingBy(DailyCount::getDay)));

		// Fill missing data by Zeros
		collect.entrySet().stream().forEach(e -> zeroFillMissingDailyName(e, allDailyNames));

		// Group values by Name and get a Map of Name to List of values
		Map<Object, List<Long>> collect2 = collect.values().stream().flatMap(o -> o.stream())
				.collect(groupingBy(DailyCount::getName, mapping(DailyCount::getValue, toList())));

		// Group values by the top private domain
		Map<Object, List<Long>> collect3 = collect2;
		if (groupByTPDomain) {
			collect3 = collect2.entrySet().stream().collect(groupingBy(e -> getDomainName(e.getKey()),
					reducing(new ArrayList<Long>(), e -> e.getValue(), (l1, l2) -> accumlateLists(l1, l2))));
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

	private void zeroFillMissingDailyName(Entry<Date, List<DailyCount>> entry, Collection<?> allNamesList) {
		Set<Object> usedNamesList = entry.getValue().stream().map(ev -> ev.getName()).collect(Collectors.toSet());
		Sets.difference(new HashSet<>(allNamesList), usedNamesList)
				.forEach(n -> entry.getValue().add(new DailyCount(entry.getKey(), n, 0l)));
	}

	private List<Long> accumlateLists(List<Long> accumlator, List<Long> list) {
		if (accumlator.isEmpty()) {
			return list;
		} else {
			for (int i = 0; i < accumlator.size(); i++) {
				accumlator.set(i, accumlator.get(i) + list.get(i));
			}
			return accumlator;
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
