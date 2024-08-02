package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.TTHApi;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TTHService implements TTHApi {
    private final TTHRepository tthRepository;

    @Override
    public TruckTrailerHistory save(TruckTrailerHistory truckTrailerHistory) {
        return tthRepository.save(truckTrailerHistory);
    }

    @Override
    public List<TruckTrailerHistoryDTO> getTrailersByStartPeriodDate(Instant startDate) {
        return List.of();
    }

    @Override
    public List<TruckTrailerHistoryDTO> getTrailersByEndPeriodDate(Instant endDate) {
        return List.of();
    }

    @Override
    public List<TruckTrailerHistoryDTO> getAllTrailersByTruckId(Long truckId) {
        return List.of();
    }

    @Override
    public List<TruckTrailerHistoryDTO> getAllTrucksByTrailerId(Long truckId) {
        return List.of();
    }
}
