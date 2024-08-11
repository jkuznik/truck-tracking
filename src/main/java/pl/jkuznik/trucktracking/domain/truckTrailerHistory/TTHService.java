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
    public List<TruckTrailerHistoryDTO> getLastMonthTruckTrailerHistory() {
        return List.of();
    }
}
