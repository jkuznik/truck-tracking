package pl.jkuznik.trucktracking.domain.shared;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import pl.jkuznik.trucktracking.domain.trailer.QTrailer;
import pl.jkuznik.trucktracking.domain.truck.QTruck;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.QTruckTrailerHistory;

public abstract class QueryRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements QueryRepository<T, ID> {

    protected EntityManager entityManager;
    protected JPAQueryFactory queryFactory;

    protected final QTruck qTruck = QTruck.truck;
    protected final QTrailer qTrailer = QTrailer.trailer;
    protected final QTruckTrailerHistory qTTH = new QTruckTrailerHistory("trailer_truck_history");

    public QueryRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
}
