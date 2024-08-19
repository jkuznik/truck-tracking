package pl.jkuznik.trucktracking.domain.shared;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface QueryRepository<T, ID> extends JpaRepository<T, ID> {
}
