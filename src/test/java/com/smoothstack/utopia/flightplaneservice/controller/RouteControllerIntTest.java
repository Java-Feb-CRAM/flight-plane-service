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
import com.smoothstack.utopia.flightplaneservice.dto.CreateRouteDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateRouteDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.DuplicateRouteException;
import com.smoothstack.utopia.flightplaneservice.exception.InvalidRouteException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.RouteNotFoundException;
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
class RouteControllerIntTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  private RouteDao routeDao;

  @Autowired
  private AirportDao airportDao;

  @Autowired
  private AirplaneTypeDao airplaneTypeDao;

  @Autowired
  private AirplaneDao airplaneDao;

  @Autowired
  private FlightDao flightDao;

  private Airport airportLAX;
  private Airport airportSFO;
  private Airport airportPDX;

  private Route createRoute(Airport origin, Airport destination) {
    Route route = new Route();
    route.setOriginAirport(origin);
    route.setDestinationAirport(destination);
    routeDao.save(route);
    return route;
  }

  private Airport createAirport(String iataId, String city) {
    Airport airport = new Airport();
    airport.setIataId(iataId);
    airport.setCity(city);
    airportDao.save(airport);
    return airport;
  }

  private void createFlight(Route route) {
    AirplaneType airplaneType = new AirplaneType();
    airplaneType.setMaxCapacity(22);
    airplaneTypeDao.save(airplaneType);
    Airplane airplane = new Airplane();
    airplane.setAirplaneType(airplaneType);
    airplaneDao.save(airplane);
    Flight flight = new Flight();
    flight.setRoute(route);
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
    airportLAX = createAirport("LAX", "Los Angeles");
    airportSFO = createAirport("SFO", "San Francisco");
    airportPDX = createAirport("PDX", "Portland");
  }

  /*
    GET Tests
   */

  @Test
  void canGetAllRoutes_whenGetRoutes_thenStatus200() throws Exception {
    createRoute(airportLAX, airportSFO);
    mvc
      .perform(get("/routes").accept(MediaType.APPLICATION_XML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(xpath("List/item[1]/originAirport/iataId").string(is("LAX")));
  }

  @Test
  void canGetRoute_whenGetRouteWithId_thenStatus200() throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    mvc
      .perform(get("/routes/{id}", route.getId()))
      .andExpect(status().isOk())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.originAirport.iataId", is("LAX")));
  }

  @Test
  void cannotGetRoute_whenGetRouteWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(get("/routes/{id}", 2))
      .andExpect(status().isNotFound())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof RouteNotFoundException
          );
        }
      );
  }

  /*
    POST Tests
   */
  @Test
  void canCreateRoute_whenPostRouteWithValidData_thenStatus201()
    throws Exception {
    CreateRouteDto createRouteDto = new CreateRouteDto();
    createRouteDto.setDestinationAirportId("LAX");
    createRouteDto.setOriginAirportId("SFO");
    mvc
      .perform(
        post("/routes")
          .content(Utils.asJsonString(createRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.originAirport.iataId", is("SFO")))
      .andExpect(
        result -> {
          Route created = Utils
            .getMapper()
            .readValue(result.getResponse().getContentAsString(), Route.class);
          Assertions.assertEquals(
            "SFO",
            routeDao
              .findById(created.getId())
              .get()
              .getOriginAirport()
              .getIataId()
          );
        }
      );
  }

  @Test
  void cannotCreateRoute_whenPostRouteWithDuplicateRoute_thenStatus409()
    throws Exception {
    createRoute(airportLAX, airportSFO);
    CreateRouteDto createRouteDto = new CreateRouteDto();
    createRouteDto.setOriginAirportId("LAX");
    createRouteDto.setDestinationAirportId("SFO");
    mvc
      .perform(
        post("/routes")
          .content(Utils.asJsonString(createRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isConflict())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof DuplicateRouteException
          );
        }
      );
  }

  @Test
  void cannotCreateRoute_whenPostRouteWithInvalidOriginAirport_thenStatus404()
    throws Exception {
    CreateRouteDto createRouteDto = new CreateRouteDto();
    createRouteDto.setOriginAirportId("IAH");
    createRouteDto.setDestinationAirportId("SFO");
    mvc
      .perform(
        post("/routes")
          .content(Utils.asJsonString(createRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportNotFoundException
          )
      );
  }

  @Test
  void cannotCreateRoute_whenPostRouteWithInvalidDestinationAirport_thenStatus404()
    throws Exception {
    CreateRouteDto createRouteDto = new CreateRouteDto();
    createRouteDto.setOriginAirportId("LAX");
    createRouteDto.setDestinationAirportId("XYZ");
    mvc
      .perform(
        post("/routes")
          .content(Utils.asJsonString(createRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportNotFoundException
          )
      );
  }

  @Test
  void cannotCreateRoute_whenPostRouteWithSameOriginAndDestination_thenStatus400()
    throws Exception {
    CreateRouteDto createRouteDto = new CreateRouteDto();
    createRouteDto.setOriginAirportId("LAX");
    createRouteDto.setDestinationAirportId("LAX");
    mvc
      .perform(
        post("/routes")
          .content(Utils.asJsonString(createRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isBadRequest())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof InvalidRouteException
          )
      );
  }

  /*
    PUT Tests
   */

  @Test
  void canUpdateRoute_whenPutRouteWithValidId_thenStatus204() throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    UpdateRouteDto updateRouteDto = new UpdateRouteDto();
    updateRouteDto.setOriginAirportId(Optional.of("PDX"));
    mvc
      .perform(
        put("/routes/{id}", route.getId())
          .content(Utils.asJsonString(updateRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent())
      .andExpect(
        result -> {
          Assertions.assertEquals(
            "PDX",
            routeDao
              .findById(route.getId())
              .get()
              .getOriginAirport()
              .getIataId()
          );
        }
      );
  }

  @Test
  void cannotUpdateRoute_whenPutRouteWithInvalidId_thenStatus404()
    throws Exception {
    UpdateRouteDto updateRouteDto = new UpdateRouteDto();
    updateRouteDto.setOriginAirportId(Optional.of("PDX"));
    mvc
      .perform(
        put("/routes/{id}", 2)
          .content(Utils.asJsonString(updateRouteDto))
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
  void cannotUpdateRoute_whenPutRouteWithInvalidOriginAirport_thenStatus404()
    throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    UpdateRouteDto updateRouteDto = new UpdateRouteDto();
    updateRouteDto.setOriginAirportId(Optional.of("XYZ"));
    mvc
      .perform(
        put("/routes/{id}", route.getId())
          .content(Utils.asJsonString(updateRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportNotFoundException
          )
      );
  }

  @Test
  void cannotUpdateRoute_whenPutRouteWithInvalidDestinationAirport_thenStatus404()
    throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    UpdateRouteDto updateRouteDto = new UpdateRouteDto();
    updateRouteDto.setDestinationAirportId(Optional.of("XYZ"));
    mvc
      .perform(
        put("/routes/{id}", route.getId())
          .content(Utils.asJsonString(updateRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportNotFoundException
          )
      );
  }

  @Test
  void cannotUpdateRoute_whenPutRouteWithSameOriginAndDestination_thenStatus400()
    throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    UpdateRouteDto updateRouteDto = new UpdateRouteDto();
    updateRouteDto.setDestinationAirportId(Optional.of("LAX"));
    mvc
      .perform(
        put("/routes/{id}", route.getId())
          .content(Utils.asJsonString(updateRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isBadRequest())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof InvalidRouteException
          )
      );
  }

  @Test
  void cannotUpdateRoute_whenPutRouteWithDuplicateRoute_thenStatus409()
    throws Exception {
    createRoute(airportLAX, airportSFO);
    Route route2 = createRoute(airportLAX, airportPDX);
    UpdateRouteDto updateRouteDto = new UpdateRouteDto();
    updateRouteDto.setDestinationAirportId(Optional.of("SFO"));
    mvc
      .perform(
        put("/routes/{id}", route2.getId())
          .content(Utils.asJsonString(updateRouteDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isConflict())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof DuplicateRouteException
          )
      );
  }

  /*
    DELETE Tests
   */
  @Test
  void canDeleteRoute_whenDeleteRouteWithValidId_thenStatus204()
    throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    mvc
      .perform(delete("/routes/{id}", route.getId()))
      .andExpect(status().isNoContent())
      .andExpect(
        result -> Assertions.assertFalse(routeDao.existsById(route.getId()))
      );
  }

  @Test
  void cannotDeleteRoute_whenDeleteRouteWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(delete("/routes/{id}", 4))
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof RouteNotFoundException
          )
      );
  }

  @Test
  void cannotDeleteRoute_whenDeleteRouteWithAssociatedFlights_thenStatus405()
    throws Exception {
    Route route = createRoute(airportLAX, airportSFO);
    createFlight(route);
    mvc
      .perform(delete("/routes/{id}", route.getId()))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof RouteDeletionNotAllowedException
          )
      );
  }
}
