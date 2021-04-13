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
import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirportDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirportDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirportDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.flightplaneservice.exception.DuplicateAirportException;
import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Route;
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
 * Mar 19 2021
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
  locations = "classpath:application-integrationtest.properties"
)
class AirportControllerIntTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  private AirportDao airportDao;

  @Autowired
  private RouteDao routeDao;

  private Airport createAirport(String iataId, String city) {
    Airport airport = new Airport();
    airport.setIataId(iataId);
    airport.setCity(city);
    airportDao.save(airport);
    return airport;
  }

  private void createRoute(Airport origin, Airport destination) {
    Route route = new Route();
    route.setOriginAirport(origin);
    route.setDestinationAirport(destination);
    routeDao.save(route);
  }

  @BeforeEach
  public void wipeDb() {
    routeDao.deleteAll();
    airportDao.deleteAll();
  }

  /*
    GET Tests
   */

  @Test
  void canGetAllAirports_whenGetAirports_thenStatus200() throws Exception {
    createAirport("IAH", "Houston");
    mvc
      .perform(get("/airports").accept(MediaType.APPLICATION_XML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(xpath("List/item[1]/iataId").string(is("IAH")));
  }

  @Test
  void canGetAirport_whenGetAirportWithId_thenStatus200() throws Exception {
    createAirport("LAX", "Los Angeles");
    mvc
      .perform(get("/airports/LAX"))
      .andExpect(status().isOk())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.iataId", is("LAX")));
  }

  @Test
  void cannotGetAirport_whenGetAirportWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(get("/airports/IAH"))
      .andExpect(status().isNotFound())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportNotFoundException
          )
      );
  }

  /*
    POST Tests
   */

  @Test
  void canCreateAirport_whenPostAirportWithValidData_thenStatus201()
    throws Exception {
    CreateAirportDto createAirportDto = new CreateAirportDto();
    createAirportDto.setIataId("SFO");
    createAirportDto.setCity("San Francisco");
    mvc
      .perform(
        post("/airports")
          .content(Utils.asJsonString(createAirportDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.iataId", is("SFO")))
      .andExpect(
        result -> {
          Assertions.assertEquals(
            "San Francisco",
            airportDao.findById("SFO").get().getCity()
          );
        }
      );
  }

  @Test
  void cannotCreateAirport_whenPostAirportWithDuplicateId_thenStatus409()
    throws Exception {
    createAirport("LAX", "Los Angeles");
    CreateAirportDto createAirportDto = new CreateAirportDto();
    createAirportDto.setIataId("LAX");
    createAirportDto.setCity("The City of Angels");
    mvc
      .perform(
        post("/airports")
          .content(Utils.asJsonString(createAirportDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isConflict())
      .andExpect(
        result ->
          Assertions.assertTrue(
            result.getResolvedException() instanceof DuplicateAirportException
          )
      );
  }

  /*
    PUT Tests
   */

  @Test
  void canUpdateAirport_whenPutAirportWithValidId_thenStatus204()
    throws Exception {
    UpdateAirportDto updateAirportDto = new UpdateAirportDto();
    updateAirportDto.setCity(Optional.of("San Francisco"));
    createAirport("SFO", "Los Angeles");
    mvc
      .perform(
        put("/airports/SFO")
          .content(Utils.asJsonString(updateAirportDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent())
      .andExpect(
        result -> {
          Assertions.assertEquals(
            "San Francisco",
            airportDao.findById("SFO").get().getCity()
          );
        }
      );
  }

  @Test
  void cannotUpdateAirport_whenPutAirportWithInvalidId_thenStatus404()
    throws Exception {
    UpdateAirportDto updateAirportDto = new UpdateAirportDto();
    updateAirportDto.setCity(Optional.of("Houston"));
    mvc
      .perform(
        put("/airports/LAX")
          .content(Utils.asJsonString(updateAirportDto))
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

  /*
    DELETE Tests
   */

  @Test
  void canDeleteAirport_whenDeleteAirportWithValidId_thenStatus204()
    throws Exception {
    createAirport("IAH", "Houston");
    mvc
      .perform(delete("/airports/IAH"))
      .andExpect(status().isNoContent())
      .andExpect(
        result -> {
          Assertions.assertFalse(airportDao.existsById("IAH"));
        }
      );
  }

  @Test
  void cannotDeleteAirport_whenDeleteAirportWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(delete("/airports/LAX"))
      .andExpect(status().isNotFound())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportNotFoundException
          );
        }
      );
  }

  @Test
  void cannotDeleteAirport_whenDeleteAirportWithAssociatedRoutes_thenStatus405()
    throws Exception {
    Airport a = createAirport("LAX", "Los Angeles");
    Airport b = createAirport("SFO", "San Francisco");
    createRoute(a, b);
    mvc
      .perform(delete("/airports/LAX"))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirportDeletionNotAllowedException
          );
        }
      );
  }
}
