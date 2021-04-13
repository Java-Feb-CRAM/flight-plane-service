package com.smoothstack.utopia.flightplaneservice.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.smoothstack.utopia.flightplaneservice.Utils;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneTypeDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dao.FlightDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneTypeNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.AirplaneType;
import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Flight;
import com.smoothstack.utopia.shared.model.Route;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Rob Maes
 * Mar 21 2021
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
  locations = "classpath:application-integrationtest.properties"
)
class AirplaneControllerIntTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  private AirplaneDao airplaneDao;

  @Autowired
  private AirplaneTypeDao airplaneTypeDao;

  @Autowired
  private AirportDao airportDao;

  @Autowired
  private RouteDao routeDao;

  @Autowired
  private FlightDao flightDao;

  private AirplaneType airplaneType777;
  private AirplaneType airplaneType737;

  private Airplane createAirplane(AirplaneType airplaneType) {
    Airplane airplane = new Airplane();
    airplane.setAirplaneType(airplaneType);
    airplaneDao.save(airplane);
    return airplane;
  }

  private AirplaneType createAirplaneType(int maxCapacity) {
    AirplaneType airplaneType = new AirplaneType();
    airplaneType.setMaxCapacity(maxCapacity);
    airplaneTypeDao.save(airplaneType);
    return airplaneType;
  }

  private void createFlight(Airplane airplane) {
    Airport airportA = new Airport();
    airportA.setIataId("AAA");
    airportA.setCity("A");
    Airport airportB = new Airport();
    airportB.setIataId("BBB");
    airportB.setCity("B");
    airportDao.save(airportA);
    airportDao.save(airportB);
    Route r = new Route();
    r.setOriginAirport(airportA);
    r.setDestinationAirport(airportB);
    routeDao.save(r);

    Flight flight = new Flight();
    flight.setRoute(r);
    flight.setAirplane(airplane);
    flight.setReservedSeats(33);
    flight.setDepartureTime(Instant.now());
    flight.setSeatPrice(55f);
    flightDao.save(flight);
  }

  @BeforeEach
  public void wipeDb() {
    flightDao.deleteAll();
    airplaneDao.deleteAll();
    airplaneTypeDao.deleteAll();
    routeDao.deleteAll();
    airportDao.deleteAll();
    airplaneType777 = createAirplaneType(777);
    airplaneType737 = createAirplaneType(737);
  }

  /*
    GET Tests
   */
  @Test
  void canGetAllAirplanes_whenGetAirplanes_thenStatus200() throws Exception {
    createAirplane(airplaneType777);
    mvc
      .perform(get("/airplanes").accept(MediaType.APPLICATION_XML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(
        xpath("List/item[1]/airplaneType/maxCapacity").string(is("777"))
      );
  }

  @Test
  void canGetAirplane_whenGetAirplaneWithId_thenStatus200() throws Exception {
    Airplane airplane = createAirplane(airplaneType777);
    mvc
      .perform(get("/airplanes/{id}", airplane.getId()))
      .andExpect(status().isOk())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.airplaneType.maxCapacity", is(777)));
  }

  @Test
  void cannotGetAirplane_whenGetAirplaneWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(get("/airplanes/{id}", 2))
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneNotFoundException
          )
      );
  }

  /*
    POST Tests
   */
  @Test
  void canCreateAirplane_whenPostAirplaneWithValidData_thenStatus201()
    throws Exception {
    CreateAirplaneDto createAirplaneDto = new CreateAirplaneDto();
    createAirplaneDto.setAirplaneTypeId(airplaneType777.getId());
    mvc
      .perform(
        post("/airplanes")
          .content(Utils.asJsonString(createAirplaneDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.airplaneType.maxCapacity", is(777)))
      .andExpect(
        result -> {
          Airplane created = Utils
            .getMapper()
            .readValue(
              result.getResponse().getContentAsString(),
              Airplane.class
            );
          Assertions.assertEquals(
            777,
            airplaneDao
              .findById(created.getId())
              .get()
              .getAirplaneType()
              .getMaxCapacity()
          );
        }
      );
  }

  @Test
  void cannotCreateAirplane_whenPostAirplaneWithInvalidAirplaneType_thenStatus404()
    throws Exception {
    CreateAirplaneDto createAirplaneDto = new CreateAirplaneDto();
    createAirplaneDto.setAirplaneTypeId(123L);
    mvc
      .perform(
        post("/airplanes")
          .content(Utils.asJsonString(createAirplaneDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneTypeNotFoundException
          )
      );
  }

  /*
    PUT Tests
   */
  @Test
  void canUpdateAirplane_whenPutAirplaneWithValidId_thenStatus204()
    throws Exception {
    Airplane airplane = createAirplane(airplaneType777);
    UpdateAirplaneDto updateAirplaneDto = new UpdateAirplaneDto();
    updateAirplaneDto.setAirplaneTypeId(Optional.of(airplaneType737.getId()));
    mvc
      .perform(
        put("/airplanes/{id}", airplane.getId())
          .content(Utils.asJsonString(updateAirplaneDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent())
      .andExpect(
        result ->
          Assertions.assertEquals(
            737,
            airplaneDao
              .findById(airplane.getId())
              .get()
              .getAirplaneType()
              .getMaxCapacity()
          )
      );
  }

  @Test
  void cannotUpdateAirplane_whenPutAirplaneWithInvalidId_thenStatus404()
    throws Exception {
    UpdateAirplaneDto updateAirplaneDto = new UpdateAirplaneDto();
    updateAirplaneDto.setAirplaneTypeId(Optional.of(airplaneType737.getId()));
    mvc
      .perform(
        put("/airplanes/{id}", 3)
          .content(Utils.asJsonString(updateAirplaneDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneNotFoundException
          )
      );
  }

  @Test
  void cannotUpdateAirplane_whenPutAirplaneWithInvalidAirplaneType_thenStatus404()
    throws Exception {
    Airplane airplane = createAirplane(airplaneType777);
    UpdateAirplaneDto updateAirplaneDto = new UpdateAirplaneDto();
    updateAirplaneDto.setAirplaneTypeId(Optional.of(123L));
    mvc
      .perform(
        put("/airplanes/{id}", airplane.getId())
          .content(Utils.asJsonString(updateAirplaneDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneTypeNotFoundException
          )
      );
  }

  /*
    DELETE Tests
   */
  @Test
  void canDeleteAirplane_whenDeleteAirplaneWithValidId_thenStatus204()
    throws Exception {
    Airplane airplane = createAirplane(airplaneType777);
    mvc
      .perform(delete("/airplanes/{id}", airplane.getId()))
      .andExpect(status().isNoContent())
      .andExpect(
        result ->
          Assertions.assertFalse(airplaneDao.existsById(airplane.getId()))
      );
  }

  @Test
  void cannotDeleteAirplane_whenDeleteAirplaneWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(delete("/airplanes/{id}", 2))
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneNotFoundException
          )
      );
  }

  @Test
  void cannotDeleteAirplane_whenDeleteAirplaneWithAssociatedFlights_thenStatus405()
    throws Exception {
    Airplane airplane = createAirplane(airplaneType777);
    createFlight(airplane);
    mvc
      .perform(delete("/airplanes/{id}", airplane.getId()))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneDeletionNotAllowedException
          )
      );
  }
}
