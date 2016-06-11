package cobone.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import cobone.model.Deal;

public interface DealRepo extends JpaRepository<Deal, Long> {
}
