//package pl.jkuznik.trucktracking.domain.truckTrailerHistory;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.TTHApi;
//import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//class TTHService implements TTHApi {
//    private final TTHRepositoryImpl tthRepository;
//
//    @Override
//    public Page<TruckTrailerHistoryDTO> getLastMonthTruckTrailerHistory(Pageable pageable) {
//        return new PageImpl<>(List.of());
//    }
//}
