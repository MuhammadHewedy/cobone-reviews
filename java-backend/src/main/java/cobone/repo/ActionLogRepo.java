package cobone.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cobone.model.ActionLog;
import cobone.model.ActionLog.Action;
import cobone.model.helper.DailyCount;

public interface ActionLogRepo extends JpaRepository<ActionLog, Long> {

	@Query("select new cobone.model.helper.DailyCount(date(o.created), o.action, count (*)) "
			+ "from ActionLog o where o.referrer is not null group by date(o.created), o.action")
	public List<DailyCount> getAllActions();

	@Query("select new cobone.model.helper.DailyCount(date(o.created), o.referrer, count (*)) "
			+ "from ActionLog o where o.referrer is not null and o.action = :action group by date(o.created), o.referrer")
	public List<DailyCount> getReferrerByAction(@Param("action") Action action);

	@Query("select distinct o.referrer from ActionLog o where o.referrer is not null")
	public List<String> findDistinctReferrer();
}
