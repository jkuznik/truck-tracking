package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TruckService implements TruckApi {

    private final TruckRepository truckRepository;
    private final TrailerRepository trailerRepository;
    private final TTHRepository tthRepository;

    @Override
    public TruckDTO addTruck(AddTruckCommand newTruck) {
        return convert(truckRepository.save(new Truck(
                UUID.randomUUID(),
                newTruck.registerPlateNumber())));
    }

    @Override
    public TruckDTO getTruckByBusinessId(UUID uuid) {
        return convert(truckRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " + uuid + " not found")));
    }

    @Override
    public List<TruckDTO> getAllTrucks() {
        return truckRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<TruckDTO> getTrucksByDateRange(Instant startDate, Instant endDate) {
        return truckRepository.findTrucksByDateRange(startDate, endDate).stream()
                .map(this::convert)
                .toList();
    }

    @Transactional
    @Override
    public TruckDTO updateTruckByBusinessId(UUID uuid, UpdateTruckCommand updateTruckCommand) throws Exception {
        Truck truck = truckRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " + uuid + " not found"));
        Optional<Trailer> trailer = Optional.empty();

        if (updateTruckCommand.trailerId().isPresent()) {

            trailer = Optional.of(trailerRepository.findByBusinessId(updateTruckCommand.trailerId().get())
                    .orElseThrow(() -> new NoSuchElementException("Trailer with business id "
                            + updateTruckCommand.trailerId() + " not found")));

            // TODO dodać obsługę wyjątków
//
//            if (trailer.get().isInUse(Instant.now())) {
//                throw new Exception("Trailer is currently in use");
//            }
        }

        if (updateTruckCommand.startPeriod().isPresent() && updateTruckCommand.endPeriod().isPresent()) {
            if (updateTruckCommand.endPeriod().get().isBefore(updateTruckCommand.startPeriod().get())) {
                throw new Exception("End period is before start period");
            }
        }

        truck.setStartPeriodDate(updateTruckCommand.startPeriod().orElse(null));
        truck.setEndPeriodDate(updateTruckCommand.endPeriod().orElse(null));
        truck.setCurrentTrailerBusinessId(updateTruckCommand.trailerId().orElse(null));


        var tth = new TruckTrailerHistory();

        tth.setTruck(truck);
        if (updateTruckCommand.startPeriod().isPresent())
            tth.setStartPeriodDate(updateTruckCommand.startPeriod().get());
        if (updateTruckCommand.endPeriod().isPresent()) tth.setEndPeriodDate(updateTruckCommand.endPeriod().get());
        if (updateTruckCommand.trailerId().isPresent()) tth.setTrailer(trailer.get());

        tthRepository.save(tth);

        return convert(truckRepository.save(truck));
    }

    @Transactional
    @Override
    public void deleteTruckByBusinessId(UUID uuid) {
        truckRepository.deleteByBusinessId(uuid);
    }

    private TruckDTO convert(Truck truck) {
        return new TruckDTO(
                truck.getRegisterPlateNumber(),
                truck.getBusinessId(),
                truck.getStartPeriodDate(),
                truck.getEndPeriodDate(),
                truck.getCurrentTrailerBusinessId());
    }
}
