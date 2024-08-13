package pl.jkuznik.trucktracking.domain.shared.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import pl.jkuznik.trucktracking.domain.shared.QueryRepository;

public class QueryRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements QueryRepository<T, ID> {

    EntityManager entityManager;
    JPAQueryFactory queryFactory;


    public QueryRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public QueryRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public T findByIdMandatory(ID id) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<T> entities) {
        super.deleteInBatch(entities);
    }
}
