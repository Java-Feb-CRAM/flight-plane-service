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
import com.smoothstack.utopia.flightplaneservice.dto.CreateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateAirplaneTypeDto;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneTypeDeletionNotAllowedException;
import com.smoothstack.utopia.flightplaneservice.exception.AirplaneTypeNotFoundException;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.AirplaneType;
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
class AirplaneTypeControllerIntTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  private AirplaneTypeDao airplaneTypeDao;

  @Autowired
  private AirplaneDao airplaneDao;

  private AirplaneType createAirplaneType(int maxCapacity) {
    AirplaneType airplaneType = new AirplaneType();
    airplaneType.setMaxCapacity(maxCapacity);
    airplaneTypeDao.save(airplaneType);
    return airplaneType;
  }

  private void createAirplane(AirplaneType airplaneType) {
    Airplane airplane = new Airplane();
    airplane.setAirplaneType(airplaneType);
    airplaneDao.save(airplane);
  }

  @BeforeEach
  public void wipeDb() {
    airplaneDao.deleteAll();
    airplaneTypeDao.deleteAll();
  }

  /*
    GET Tests
   */

  @Test
  void canGetAllAirplaneTypes_whenGetAirplaneTypes_thenStatus200()
    throws Exception {
    createAirplaneType(300);
    mvc
      .perform(get("/airplane_types").accept(MediaType.APPLICATION_XML))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
      .andExpect(xpath("List/item[1]/maxCapacity").string(is("300")));
  }

  @Test
  void canGetAirplaneType_whenGetAirplaneTypeWithId_thenStatus200()
    throws Exception {
    AirplaneType airplaneType = createAirplaneType(20);
    mvc
      .perform(get("/airplane_types/{id}", airplaneType.getId()))
      .andExpect(status().isOk())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.maxCapacity", is(20)));
  }

  @Test
  void cannotGetAirplaneType_whenGetAirplaneTypeWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(get("/airplane_types/{id}", 1))
      .andExpect(status().isNotFound())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneTypeNotFoundException
          );
        }
      );
  }

  /*
    POST Tests
   */
  @Test
  void canCreateAirplaneType_whenPostAirplaneTypeWithValidData_thenStatus201()
    throws Exception {
    CreateAirplaneTypeDto createAirplaneTypeDto = new CreateAirplaneTypeDto();
    createAirplaneTypeDto.setMaxCapacity(300);
    mvc
      .perform(
        post("/airplane_types")
          .content(Utils.asJsonString(createAirplaneTypeDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andExpect(
        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      )
      .andExpect(jsonPath("$.maxCapacity", is(300)))
      .andExpect(
        result -> {
          AirplaneType created = Utils
            .getMapper()
            .readValue(
              result.getResponse().getContentAsString(),
              AirplaneType.class
            );
          Assertions.assertEquals(
            300,
            airplaneTypeDao.findById(created.getId()).get().getMaxCapacity()
          );
        }
      );
  }

  /*
    PUT Tests
   */
  @Test
  void canUpdateAirplaneType_whenPutAirplaneTypeWithValidId_thenStatus204()
    throws Exception {
    AirplaneType airplaneType = createAirplaneType(1234);
    UpdateAirplaneTypeDto updateAirplaneTypeDto = new UpdateAirplaneTypeDto();
    updateAirplaneTypeDto.setMaxCapacity(Optional.of(1111));
    mvc
      .perform(
        put("/airplane_types/{id}", airplaneType.getId())
          .content(Utils.asJsonString(updateAirplaneTypeDto))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent())
      .andExpect(
        result -> {
          Assertions.assertEquals(
            1111,
            airplaneTypeDao
              .findById(airplaneType.getId())
              .get()
              .getMaxCapacity()
          );
        }
      );
  }

  @Test
  void cannotUpdateAirplaneType_whenPutAirplaneTypeWithInvalidId_thenStatus404()
    throws Exception {
    UpdateAirplaneTypeDto updateAirplaneTypeDto = new UpdateAirplaneTypeDto();
    updateAirplaneTypeDto.setMaxCapacity(Optional.of(123));
    mvc
      .perform(
        put("/airplane_types/{id}", 2)
          .content(Utils.asJsonString(updateAirplaneTypeDto))
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
  void canDeleteAirplaneType_whenDeleteAirplaneTypeWithValidId_thenStatus204()
    throws Exception {
    AirplaneType airplaneType = createAirplaneType(999);
    mvc
      .perform(delete("/airplane_types/{id}", airplaneType.getId()))
      .andExpect(status().isNoContent())
      .andExpect(
        result -> {
          Assertions.assertFalse(
            airplaneTypeDao.existsById(airplaneType.getId())
          );
        }
      );
  }

  @Test
  void cannotDeleteAirplaneType_whenDeleteAirplaneTypeWithInvalidId_thenStatus404()
    throws Exception {
    mvc
      .perform(delete("/airplane_types/{id}", 4))
      .andExpect(status().isNotFound())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneTypeNotFoundException
          );
        }
      );
  }

  @Test
  void cannotDeleteAirplaneType_whenDeleteAirplaneTypeWithAssociatedAirplanes_thenStatus405()
    throws Exception {
    AirplaneType airplaneType = createAirplaneType(345);
    createAirplane(airplaneType);
    mvc
      .perform(delete("/airplane_types/{id}", airplaneType.getId()))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(
        result -> {
          Assertions.assertTrue(
            result.getResolvedException() instanceof AirplaneTypeDeletionNotAllowedException
          );
        }
      );
  }
}
