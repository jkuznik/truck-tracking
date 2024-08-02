package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                newTruck.registerPlateNumber(),
                UUID.randomUUID(),
                newTruck.length(),
                newTruck.height(),
                newTruck.weight())));
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
    public TruckDTO updateTruckByBusinessId(UUID uuid, UpdateTruckCommand updateTruckCommand) {
        Truck truck = truckRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " + uuid + " not found"));

        //TODO dodać logikę nie pozwalającą na ustawienie endPeriod mniejszy od startPeriod
        truck.setInUse(updateTruckCommand.isUsed());
        if (truck.isInUse()) {
            truck.setStartPeriodDate(updateTruckCommand.startPeriod());
            truck.setEndPeriodDate(updateTruckCommand.endPeriod());
        } else {
            truck.setStartPeriodDate(null);
            truck.setEndPeriodDate(null);
        }

        var tth = new TruckTrailerHistory();

        tth.setTruck(truck);
        tth.setStartPeriodDate(updateTruckCommand.startPeriod());
        tth.setEndPeriodDate(updateTruckCommand.endPeriod());
        tth.setTrailer(trailerRepository.findByBusinessId(updateTruckCommand.trailerId()).orElseThrow(NoSuchElementException::new));

        tthRepository.save(tth);

        return convert(truckRepository.save(truck));
    }

    @Override
    public void deleteTruckByBusinessId(UUID uuid) {

    }

    private TruckDTO convert(Truck truck) {
        return new TruckDTO(
                truck.getRegisterPlateNumber(),
                truck.getBusinessId(),
                truck.isInUse(),
                truck.getStartPeriodDate(),
                truck.getEndPeriodDate(),
                truck.getLength(),
                truck.getHeight(),
                truck.getWeight());
    }
}
