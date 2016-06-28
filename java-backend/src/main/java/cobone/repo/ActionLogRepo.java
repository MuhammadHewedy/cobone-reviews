package cobone.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cobone.model.ActionLog;

public interface ActionLogRepo extends JpaRepository<ActionLog, Long> {

}
