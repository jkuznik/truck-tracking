package pl.jkuznik.trucktracking.domain.bootstrap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepositoryImpl;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Component
@Profile("test")
public class Bootstrap {

    TrailerRepository trailerRepository;
    TruckRepository truckRepository;
    TTHRepositoryImpl tthRepository;

    public Bootstrap(TrailerRepository trailerRepository, TruckRepository truckRepository, TTHRepositoryImpl tthRepository) {
        this.trailerRepository = trailerRepository;
        this.truckRepository = truckRepository;
        this.tthRepository = tthRepository;

        run();
    }

    public void run() {
        Random random = new Random();

        if (trailerRepository.findAll().size() < 20) {
            for (int i = 0; i < 50; i++) {
                String trailerPlateNumber = "TRAILER" + i;
                String truckPlateNumber = "TRUCK" + i;
                UUID trailerId = UUID.randomUUID();
                UUID truckId = UUID.randomUUID();
                Trailer trailer = new Trailer(trailerId, trailerPlateNumber);
                Truck truck = new Truck(truckId, truckPlateNumber);
                TruckTrailerHistory tth = new TruckTrailerHistory();

                int a = random.nextInt(4);
                if (a == 1) {
                    trailerRepository.save(trailer);
                    truckRepository.save(truck);
                    continue;
                }

                boolean startPerdiodAssigned = random.nextBoolean();
                boolean endPerdiodAssigned = random.nextBoolean();

                Instant startPerdiodDate = Instant.now().minusSeconds(random.nextInt(3600 * 24 * 90));
                Instant endPerdiodDate = Instant.now().minusSeconds(random.nextInt(3600 * 24 * 60));

                if (startPerdiodAssigned && !endPerdiodAssigned) {
                    trailer.setStartPeriodDate(startPerdiodDate);
                    trailer.setCurrentTruckBusinessId(truckId);

                    truck.setStartPeriodDate(startPerdiodDate);
                    truck.setCurrentTrailerBusinessId(trailerId);

                    tth.setStartPeriodDate(startPerdiodDate);
                    tth.setTrailer(trailer);
                    tth.setTruck(truck);

                    trailerRepository.save(trailer);
                    truckRepository.save(truck);
                    tthRepository.save(tth);
                }

                if (!startPerdiodAssigned && endPerdiodAssigned) {
                    trailer.setEndPeriodDate(endPerdiodDate);
                    trailer.setCurrentTruckBusinessId(truckId);

                    truck.setEndPeriodDate(endPerdiodDate);
                    truck.setCurrentTrailerBusinessId(trailerId);

                    tth.setEndPeriodDate(endPerdiodDate);
                    tth.setTrailer(trailer);
                    tth.setTruck(truck);

                    trailerRepository.save(trailer);
                    truckRepository.save(truck);
                    tthRepository.save(tth);
                }

                if (endPerdiodAssigned && startPerdiodAssigned) {
                    if (startPerdiodDate.isBefore(endPerdiodDate)) {
                        trailer.setStartPeriodDate(startPerdiodDate);
                        trailer.setEndPeriodDate(endPerdiodDate);
                        trailer.setCurrentTruckBusinessId(truckId);

                        truck.setStartPeriodDate(startPerdiodDate);
                        truck.setEndPeriodDate(endPerdiodDate);
                        truck.setCurrentTrailerBusinessId(trailerId);

                        tth.setStartPeriodDate(startPerdiodDate);
                        tth.setEndPeriodDate(endPerdiodDate);
                        tth.setTrailer(trailer);
                        tth.setTruck(truck);

                        trailerRepository.save(trailer);
                        truckRepository.save(truck);
                        tthRepository.save(tth);
                    }
                }
            }
        }
    }
}
