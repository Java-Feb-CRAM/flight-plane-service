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

import com.smoothstack.utopia.flightplaneservice.BookingDao;
import com.smoothstack.utopia.flightplaneservice.Utils;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneTypeDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dao.FlightDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateFlightDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateFlightDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.FlightDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.FlightNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.AirplaneType;
import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Booking;
import com.smoothstack.utopia.shared.model.Flight;
import com.smoothstack.utopia.shared.model.Route;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
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
class FlightControllerIntTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  private FlightDao flightDao;

  @Autowired
  private RouteDao routeDao;

  @Autowired
  private AirportDao airportDao;

  @Autowired
  private AirplaneDao airplaneDao;

  @Autowired
  private AirplaneTypeDao airplaneTypeDao;

  @Autowired
  private BookingDao bookingDao;

  private Airport airportLAX;
  private Airport airportSFO;
  private Route routeLAXtoSFO;
  private AirplaneType airplaneType777;
  private Airplane airplane;

  private Flight createFlight(
    Route route,
    Airplane airplane,
    Instant departureTime,
    Float seatPrice,
    Integer reservedSeats
  ) {
    Flight flight = new Flight();
    flight.setRoute(route);
    flight.setAirplane(airplane);
    flight.setDepartureTime(departureTime);
    flight.setSeatPrice(seatPrice);
    flight.setReservedSeats(reservedSeats);
    flightDao.save(flight);
    return flight;
  }

  private Flight createFlight() {
    return createFlight(routeLAXtoSFO, airplane, Instant.now(), 55f, 23);
  }

  private Route createRoute(Airport origin, Airport destination) {
    Route route = new Route();
    route.setOriginAirport(origin);
    route.setDestinationAirport(destination);
    routeDao.save(route);
    return route;
  }

  private Airplane createAirplane(AirplaneType airplaneType) {
    Airplane airplane = new Airplane();
    airplane.setAirplaneType(airplaneType);
    airplaneDao.save(airplane);
    return airplane;
  }

  private Airport createAirport(String iataId, String city) {
    Airport airport = new Airport();
    airport.setIataId(iataId);
    airport.setCity(city);
    airportDao.save(airport);
    return airport;
  }

  private AirplaneType createAirplaneType(Integer maxCapacity) {
    AirplaneType airplaneType = new AirplaneType();
    airplaneType.setMaxCapacity(maxCapacity);
    airplaneTypeDao.save(airplaneType);
    return airplaneType;
  }

  @BeforeEach
  public void wipeDb() {
    flightDao.deleteAll();
    airplaneDao.deleteAll();
    routeDao.deleteAll();
    airplaneTypeDao.deleteAll();
    airportDao.deleteAll();
    airportLAX = createAirport("LAX", "Los Angeles");
    airportSFO = createAirport("SFO", "San Francisco");
    routeLAXtoSFO = createRoute(airportLAX, airportSFO);
    airplaneType777 = createAirplaneType(777);
    airplane = createAirplane(airplaneType777);
  }

  /*
    GET Tests
   */
  @Test
  void canGetAllFlights_whenGetFlights_thenStatus200() throws Exception {
    createFlight();
    mvc
      .perform(get("/flights").accept(MediaType.APPLICATION_XML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(
        xpath("List/item[1]/route/originAirport/iataId").string(is("LAX"))
      );
  }

  @Test
  void canGetFlight_whenGetFlightWithId_thenStatus200() throws Exception {
    Flight flight = createFlight();
    mvc
      .perform(get("/flights/{id}", flight.getId()))
      .andExpect(status().isOk())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.route.originAirport.iataId", is("LAX")));
  }

  @Test
  void cannotGetFlight_whenGetFlightWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(get("/flights/{id}", 4))
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof FlightNotFoundException
          )
      );
  }

  /*
    POST Tests
   */
  @Test
  void canCreateFlight_whenPostFlightWithValidData_thenStatus201()
    throws Exception {
    CreateFlightDto createFlightDto = new CreateFlightDto();
    createFlightDto.setDepartureTime(Instant.now());
    createFlightDto.setReservedSeats(45);
    createFlightDto.setSeatPrice(9.99f);
    createFlightDto.setAirplaneId(airplane.getId());
    createFlightDto.setRouteId(routeLAXtoSFO.getId());
    mvc
      .perform(
        post("/flights")
          .content(Utils.asJsonString(createFlightDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.route.originAirport.iataId", is("LAX")))
      .andExpect(
        result -> {
          Flight created = Utils
            .getMapper()
            .readValue(result.getResponse().getContentAsString(), Flight.class);
          Assertions.assertEquals(
            "LAX",
            flightDao
              .findById(created.getId())
              .get()
              .getRoute()
              .getOriginAirport()
              .getIataId()
          );
        }
      );
  }

  @Test
  void cannotCreateFlight_whenPostFlightWithInvalidRoute_thenStatus404()
    throws Exception {
    CreateFlightDto createFlightDto = new CreateFlightDto();
    createFlightDto.setDepartureTime(Instant.now());
    createFlightDto.setReservedSeats(45);
    createFlightDto.setSeatPrice(9.99f);
    createFlightDto.setAirplaneId(airplane.getId());
    createFlightDto.setRouteId(100L);
    mvc
      .perform(
        post("/flights")
          .content(Utils.asJsonString(createFlightDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof RouteNotFoundException
          )
      );
  }

  @Test
  void cannotCreateFlight_whenPostFlightWithInvalidAirplane_thenStatus404()
    throws Exception {
    CreateFlightDto createFlightDto = new CreateFlightDto();
    createFlightDto.setDepartureTime(Instant.now());
    createFlightDto.setReservedSeats(45);
    createFlightDto.setSeatPrice(9.99f);
    createFlightDto.setAirplaneId(22L);
    createFlightDto.setRouteId(routeLAXtoSFO.getId());
    mvc
      .perform(
        post("/flights")
          .content(Utils.asJsonString(createFlightDto))
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

  /*
    PUT Tests
   */

  @Test
  void canUpdateFlight_whenPutFlightWithValidId_thenStatus204()
    throws Exception {
    Flight flight = createFlight();
    UpdateFlightDto updateFlightDto = new UpdateFlightDto();
    updateFlightDto.setSeatPrice(Optional.of(1337f));
    mvc
      .perform(
        put("/flights/{id}", flight.getId())
          .content(Utils.asJsonString(updateFlightDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent())
      .andExpect(
        result ->
          Assertions.assertEquals(
            1337f,
            flightDao.findById(flight.getId()).get().getSeatPrice()
          )
      );
  }

  @Test
  void cannotUpdateFlight_whenPutFlightWithInvalidId_thenStatus404()
    throws Exception {
    UpdateFlightDto updateFlightDto = new UpdateFlightDto();
    updateFlightDto.setSeatPrice(Optional.of(1337f));
    mvc
      .perform(
        put("/flights/{id}", 3)
          .content(Utils.asJsonString(updateFlightDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof FlightNotFoundException
          )
      );
  }

  @Test
  void cannotUpdateFlight_whenPutFlightWithInvalidRoute_thenStatus404()
    throws Exception {
    Flight flight = createFlight();
    UpdateFlightDto updateFlightDto = new UpdateFlightDto();
    updateFlightDto.setRouteId(Optional.of(2L));
    mvc
      .perform(
        put("/flights/{id}", flight.getId())
          .content(Utils.asJsonString(updateFlightDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof RouteNotFoundException
          )
      );
  }

  @Test
  void cannotUpdateFlight_whenPutFlightWithInvalidAirplane_thenStatus404()
    throws Exception {
    Flight flight = createFlight();
    UpdateFlightDto updateFlightDto = new UpdateFlightDto();
    updateFlightDto.setAirplaneId(Optional.of(25L));
    mvc
      .perform(
        put("/flights/{id}", flight.getId())
          .content(Utils.asJsonString(updateFlightDto))
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

  /*
    DELETE Tests
   */
  @Test
  void canDeleteFlight_whenDeleteFlightWithValidId_thenStatus204()
    throws Exception {
    Flight flight = createFlight();
    mvc
      .perform(delete("/flights/{id}", flight.getId()))
      .andExpect(status().isNoContent())
      .andExpect(
        result -> Assertions.assertFalse(flightDao.existsById(flight.getId()))
      );
  }

  @Test
  void cannotDeleteFlight_whenDeleteFlightWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(delete("/flights/{id}", 4213))
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof FlightNotFoundException
          )
      );
  }

  @Test
  void cannotDeleteFlight_whenDeleteFlightWithAssociatedBookings_thenStatus405()
    throws Exception {
    Booking booking = new Booking();
    bookingDao.save(booking);
    Flight flight = new Flight();
    flight.setRoute(routeLAXtoSFO);
    flight.setAirplane(airplane);
    flight.setSeatPrice(35f);
    flight.setReservedSeats(22);
    flight.setDepartureTime(Instant.now());
    flight.setBookings(Set.of(booking));
    flightDao.save(flight);
    mvc
      .perform(delete("/flights/{id}", flight.getId()))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof FlightDeletionNotAllowedException
          )
      );
  }
}
