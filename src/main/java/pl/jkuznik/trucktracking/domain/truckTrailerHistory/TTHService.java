package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.TTHApi;

@Service
@RequiredArgsConstructor
public class TTHService implements TTHApi {
    private final TTHRepository tthRepository;

    @Override
    public TruckTrailerHistory save(TruckTrailerHistory truckTrailerHistory) {
        return tthRepository.save(truckTrailerHistory);
    }
}
