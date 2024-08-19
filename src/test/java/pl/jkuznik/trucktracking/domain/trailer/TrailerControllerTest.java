package pl.jkuznik.trucktracking.domain.trailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.jkuznik.trucktracking.domain.shared.ControllerExceptionHandler;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.security.config.TestSecurityConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private Page<TrailerDTO> trailerDTOPage;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrailerService trailerService;

    @BeforeEach
    void setUp() {
        List<TrailerDTO> trailerDTOList = new ArrayList<>();

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

        trailerDTOPage = new PageImpl<>(trailerDTOList);
    }

    @Nested
    class GetMethodsTests {

        @Test
        void getTrailersWhenTrailerListIsNotEmpty() throws Exception {
            //when
            when(trailerService.getAllTrailers(any(Integer.class), any(Integer.class))).thenReturn(trailerDTOPage);

            //then
            mvc.perform(get(BASE_URL)
                            .param("pageNumber", "1")
                            .param("pageSize", "25"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(content().json(objectMapper.writeValueAsString(trailerDTOPage)))
                    .andExpect(jsonPath("$.content[0].trailerPlateNumber").value("TRAILER001"))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(3))
                    .andExpect(jsonPath("$.numberOfElements").value(3));

        }

        @Test
        void getTrailersWhenTrailerListIsEmpty() throws Exception {
            //when
            when(trailerService.getAllTrailers(any(Integer.class), any(Integer.class))).thenReturn(Page.empty());

            //then
            mvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        void getTrailerWhenTrailerExist() throws Exception {
            //when
            when(trailerService.getTrailerByBusinessId(TRAILER_ID)).thenReturn(trailerDTO);

            //then
            mvc.perform(get(BASE_URL_SLASH + TRAILER_ID)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(trailerDTO)))
                    .andExpect(jsonPath("$.trailerPlateNumber").value(TRAILER_REGISTER_NUMBER));
        }

        @Test
        void getTrailerWhenTrailerNotExist() throws Exception {
            //when
            when(trailerService.getTrailerByBusinessId(TRAILER_ID)).thenReturn(null);

            //then
            mvc.perform(get(BASE_URL_SLASH + TRAILER_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("No trailer with business id " + TRAILER_ID));
        }

    }

    @Nested
    class PostMethodsTests {

        @Test
        void createTrailerWhenTrailerNotExist() throws Exception {
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
        void createTrailerWhenTrailerExist() throws Exception {
            //given
            var addCommand = new AddTrailerCommand(TRAILER_REGISTER_NUMBER);

            //when
            when(trailerService.addTrailer(addCommand)).thenThrow(IllegalStateException.class);

            //then
            mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addCommand)))
                    .andExpect(status().isBadRequest());
        }
    }
}