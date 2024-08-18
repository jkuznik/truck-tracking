package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.jkuznik.trucktracking.domain.shared.QueryRepositoryImpl;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TTHRepositoryImpl extends QueryRepositoryImpl<TruckTrailerHistory, Long> implements TTHRepository {

    public TTHRepositoryImpl(EntityManager entityManager) {
        super(TruckTrailerHistory.class, entityManager);
    }

    public Page<Truck> getTruckUsedInLastMonth() {

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(now, zoneId);
        Instant monthAgo = zdt.minusMonths(1).toInstant();

        JPAQuery<TruckTrailerHistory> query = new JPAQuery<>(entityManager);

        List<TruckTrailerHistory> fetch = query.select(qTTH).from(qTTH)
                .where
                                        (Expressions.asDate(qTTH.startPeriodDate).between(monthAgo, now)
                                .or
                                        (Expressions.asDate(qTTH.endPeriodDate).between(monthAgo, now))
                                .or
                                        (Expressions.asDate(qTTH.startPeriodDate).before(monthAgo).and(Expressions.asDate(qTTH.endPeriodDate).isNull()))
                                .or
                                        (Expressions.asDate(qTTH.startPeriodDate).isNull().and(Expressions.asDate(qTTH.endPeriodDate).after(now)))
                                .or
                                        (Expressions.asDate(qTTH.startPeriodDate).before(monthAgo).and(Expressions.asDate(qTTH.endPeriodDate).after(now)))
                        )
                .fetch();


        List<TruckTrailerHistory> fetchedTTH = new ArrayList<>(fetch);

        return new PageImpl<>(fetchedTTH.stream()
                .map(TruckTrailerHistory::getTruck)
                .toList());
    }
}
