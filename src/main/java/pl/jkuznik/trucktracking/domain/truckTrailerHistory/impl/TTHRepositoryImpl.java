package pl.jkuznik.trucktracking.domain.truckTrailerHistory.impl;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.jkuznik.trucktracking.domain.shared.QueryRepositoryImpl;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

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
        List<TruckTrailerHistory> fetch = query.from(qTTH)
                .where(qTTH.startPeriodDate.after(monthAgo))
                .fetch();


        List<TruckTrailerHistory> fetchedTTH = new ArrayList<>(fetch);

        return new PageImpl<>(fetchedTTH.stream()
                .map(TruckTrailerHistory::getTruck)
                .toList());
    }
}
