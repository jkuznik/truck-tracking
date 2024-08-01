package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TruckService implements TruckApi {

    private final TruckRepository truckRepository;

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

    @Override
    public TrailerDTO updateTruckByBusinessId(UUID uuid, UpdateTruckCommand newTruck) {
        return null;
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
