package pl.jkuznik.trucktracking.domain.trailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.jkuznik.trucktracking.domain.shared.ControllerExceptionHandler;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.security.config.TestSecurityConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrailerController.class)
@Import({TestSecurityConfig.class, ControllerExceptionHandler.class})
class TrailerControllerTest {

    private final String BASE_URL = "/trailer";
    private final String BASE_URL_SLASH = BASE_URL + "/";
    private final String TRAILER_REGISTER_NUMBER = "TRAILER001";
    private final UUID TRAILER_ID = UUID.randomUUID();
    private final UUID TRUCK_ID = UUID.randomUUID();
    private final Instant START_PERIOD_TIME = Instant.parse("2024-01-01T00:00:00Z");
    private final Instant END_PERIOD_TIME = Instant.parse("2024-01-01T00:00:00Z");
    private TrailerDTO trailerDTO;
    private TrailerDTO trailerDTO2;
    private TrailerDTO trailerDTO3;
    private List<TrailerDTO> trailerDTOList = new ArrayList<>();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrailerService trailerService;

    @BeforeEach
    void setUp() {
        trailerDTO = TrailerDTO.builder()
                .trailerPlateNumber(TRAILER_REGISTER_NUMBER)
                .businessId(TRAILER_ID)
                .isCrossHitch(true)
                .startPeriod(START_PERIOD_TIME)
                .endPeriod(END_PERIOD_TIME)
                .currentTruckBusinessId(TRUCK_ID)
                .build();

        trailerDTO2 = TrailerDTO.builder()
                .trailerPlateNumber("TRAILER002")
                .businessId(UUID.randomUUID())
                .isCrossHitch(true)
                .startPeriod(START_PERIOD_TIME)
                .endPeriod(END_PERIOD_TIME)
                .currentTruckBusinessId(UUID.randomUUID())
                .build();

        trailerDTO3 = TrailerDTO.builder()
                .trailerPlateNumber("TRAILER003")
                .businessId(UUID.randomUUID())
                .isCrossHitch(true)
                .startPeriod(START_PERIOD_TIME)
                .endPeriod(END_PERIOD_TIME)
                .currentTruckBusinessId(UUID.randomUUID())
                .build();

        trailerDTOList.add(trailerDTO);
        trailerDTOList.add(trailerDTO2);
        trailerDTOList.add(trailerDTO3);
    }

    @Nested
    class GetMethodsTests {

        @Test
        void getTrailers() throws Exception {
            //when
            when(trailerService.getAllTrailers()).thenReturn(trailerDTOList);

            //then
            mvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(content().json(objectMapper.writeValueAsString(trailerDTOList)))
                    .andExpect(jsonPath("$[0].trailerPlateNumber").value(TRAILER_REGISTER_NUMBER))
                    .andExpect(jsonPath("$[1].trailerPlateNumber").value("TRAILER002"))
                    .andExpect(jsonPath("$[2].trailerPlateNumber").value("TRAILER003"));

        }

        @Test
        void getTrailer() throws Exception {
            //when
            when(trailerService.getTrailerByBusinessId(TRAILER_ID)).thenReturn(trailerDTO);

            //then
            mvc.perform(get(BASE_URL_SLASH + TRAILER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(trailerDTO)))
                    .andExpect(jsonPath("$.trailerPlateNumber").value(TRAILER_REGISTER_NUMBER));
        }

        @Test
        void getTrailersActualState() {
        }

        @Test
        void getTrailersHistory() {
        }
    }

    @Test
    void createTrailer() throws Exception {
        //given
        var addCommand = new AddTrailerCommand(TRAILER_REGISTER_NUMBER);

        //when
        when(trailerService.addTrailer(addCommand)).thenReturn(trailerDTO);

        //then
        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommand)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(trailerDTO)))
                .andExpect(jsonPath("$.trailerPlateNumber").value(TRAILER_REGISTER_NUMBER));
    }

    @Test
    void updateTrailerByBusinessId() {
    }

    @Test
    void assignTrailerManage() {
    }

    @Test
    void unassignTrailerManage() {
    }

    @Test
    void crossHitchTrailerByBusinessId() {
    }

    @Test
    void deleteTrailer() {
    }
}