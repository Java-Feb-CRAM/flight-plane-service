package com.smoothstack.utopia.flightplaneservice.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smoothstack.utopia.flightplaneservice.Utils;
import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirportDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirportNotFoundException;
import com.smoothstack.utopia.shared.model.Airport;
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
public class AirportControllerIntTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  private AirportDao airportDao;

  private void createAirport(String iataId, String city) {
    Airport airport = new Airport();
    airport.setIataId(iataId);
    airport.setCity(city);
    airportDao.save(airport);
  }

  @BeforeEach
  public void wipeAirportDb() {
    airportDao.deleteAll();
  }

  @Test
  public void canGetAllAirportsJson_whenGetAirportsJson_thenStatus200()
    throws Exception {
    createAirport("IAH", "Houston");
    mvc
      .perform(get("/airports"))
      .andExpect(status().isOk())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$[0].iataId", is("IAH")));
  }

  @Test
  public void canGetAllAirportsXml_whenGetAirportsXml_thenStatus200()
    throws Exception {
    createAirport("IAH", "Houston");
    mvc
      .perform(get("/airports").accept(MediaType.APPLICATION_XML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(xpath("List/item[1]/iataId").string(is("IAH")));
  }

  @Test
  public void cannotGetAirport_whenGetAirportWithInvalidId_thenStatus404()
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

  @Test
  public void canCreateAirport_whenPostAirportWithValidDataJson_thenStatus201()
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
      .andExpect(jsonPath("$.iataId", is("SFO")));
  }

  @Test
  public void canCreateAirport_whenPostAirportWithValidDataXml_thenStatus201()
    throws Exception {
    CreateAirportDto createAirportDto = new CreateAirportDto();
    createAirportDto.setIataId("LAX");
    createAirportDto.setCity("Los Angeles");
    mvc
      .perform(
        post("/airports")
          .content(Utils.asXmlString(createAirportDto))
          .contentType(MediaType.APPLICATION_XML)
          .accept(MediaType.APPLICATION_XML)
      )
      .andExpect(status().isCreated())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(xpath("Airport/iataId").string(is("LAX")));
  }
}
