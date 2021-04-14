package com.smoothstack.utopia.flightplaneservice.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirplaneTypeDao;
import com.smoothstack.utopia.flightplaneservice.dao.AirportDao;
import com.smoothstack.utopia.flightplaneservice.dao.FlightDao;
import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.shared.model.Airplane;
import com.smoothstack.utopia.shared.model.AirplaneType;
import com.smoothstack.utopia.shared.model.Airport;
import com.smoothstack.utopia.shared.model.Flight;
import com.smoothstack.utopia.shared.model.Route;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Craig Saunders
 * 
 *         template by Rob Maes Mar 21 2021
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties")
class FlightControllerFlightSearchIntTest {

  private final String URI =
      "/flights/origin/%s/destination/%s/departure/%d/search/";

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

  private Airport airportJFK;
  private Airport airportDEN;
  private Airport airportORD;
  private Airport airportSLC;
  private Airport airportGCK;
  private Airport airportSFO;
  private Airport airportSEA;
  private Route routeJFKtoORD;
  private Route routeORDtoDEN;
  private Route routeDENtoSFO;
  private Route routeJFKtoSFO;
  private Route routeSFOtoJFK;
  private Route routeSFOtoDEN;
  private Route routeDENtoORD;
  private Route routeORDtoJFK;
  private Route routeJFKtoDEN;
  private Route routeDENtoJFK;
  private Route routeSEAtoJFK;
  private Route routeDENtoSEA;
  private Route routeSLCtoGCK;
  private Route routeGCKtoORD;
  private Route routeORDtoSLC;
  private Route routeSLCtoORD;
  private Route routeORDtoGCK;
  private Route routeGCKtoSLC;
  private List<Flight> flights;
  private AirplaneType airplaneType777;
  private Airplane airplane;
  private Instant unfilteredOriginDepartureInstant;
  private Instant filteredOriginDepartureInstant;
  private Instant roundTripOriginDepartureInstant;

  private Flight createFlight(Route route,
      Airplane airplane,
      Instant departureTime,
      Float seatPrice,
      Integer reservedSeats)
  {
    Flight flight = new Flight();
    flight.setRoute(route);
    flight.setAirplane(airplane);
    flight.setDepartureTime(departureTime);
    flight.setSeatPrice(seatPrice);
    flight.setReservedSeats(reservedSeats);
    flightDao.save(flight);
    return flight;
  }

  private Flight createFlight(Route route, Instant departureTime) {
    return createFlight(route, airplane, departureTime, 55f, 23);
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
    airportJFK = createAirport("JFK", "New York");
    airportORD = createAirport("ORD", "Chicago");
    airportDEN = createAirport("DEN", "Denver");
    airportSLC = createAirport("SLC", "Salt Lake City");
    airportGCK = createAirport("GCK", "Garden City");
    airportSFO = createAirport("SFO", "San Francisco");
    airportSEA = createAirport("SEA", "Seattle");

    routeJFKtoORD = createRoute(airportJFK, airportORD);
    routeORDtoDEN = createRoute(airportORD, airportDEN);
    routeDENtoSFO = createRoute(airportDEN, airportSFO);
    routeJFKtoSFO = createRoute(airportJFK, airportSFO);
    routeSFOtoJFK = createRoute(airportSFO, airportJFK);
    routeSFOtoDEN = createRoute(airportSFO, airportDEN);
    routeDENtoORD = createRoute(airportDEN, airportORD);
    routeORDtoJFK = createRoute(airportORD, airportJFK);
    routeJFKtoDEN = createRoute(airportJFK, airportDEN);
    routeDENtoJFK = createRoute(airportDEN, airportJFK);
    routeSEAtoJFK = createRoute(airportSEA, airportJFK);
    routeDENtoSEA = createRoute(airportDEN, airportSEA);
    routeSLCtoGCK = createRoute(airportSLC, airportGCK);
    routeGCKtoORD = createRoute(airportGCK, airportORD);
    routeORDtoSLC = createRoute(airportORD, airportSLC);
    routeSLCtoORD = createRoute(airportSLC, airportORD);
    routeORDtoGCK = createRoute(airportORD, airportGCK);
    routeGCKtoSLC = createRoute(airportGCK, airportSLC);

    filteredOriginDepartureInstant = Instant.ofEpochSecond(1614531600);
    unfilteredOriginDepartureInstant = Instant.ofEpochSecond(1614542400);
    roundTripOriginDepartureInstant = Instant.ofEpochSecond(1614553200);

    airplaneType777 = createAirplaneType(777);
    airplane = createAirplane(airplaneType777);

    flights = new ArrayList<Flight>();
    flights.add(
        this.createFlight(routeJFKtoORD, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeORDtoDEN, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeDENtoSFO, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeJFKtoSFO, unfilteredOriginDepartureInstant)
    );
    flights
        .add(this.createFlight(routeSFOtoJFK, roundTripOriginDepartureInstant));
    flights
        .add(this.createFlight(routeSFOtoDEN, roundTripOriginDepartureInstant));
    flights
        .add(this.createFlight(routeDENtoORD, roundTripOriginDepartureInstant));
    flights
        .add(this.createFlight(routeORDtoJFK, roundTripOriginDepartureInstant));
    flights
        .add(this.createFlight(routeJFKtoDEN, filteredOriginDepartureInstant));
    flights.add(
        this.createFlight(routeDENtoJFK, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeSEAtoJFK, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeDENtoSEA, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeSLCtoGCK, unfilteredOriginDepartureInstant)
    );
    flights
        .add(this.createFlight(routeSLCtoGCK, filteredOriginDepartureInstant));
    flights.add(
        this.createFlight(routeGCKtoORD, unfilteredOriginDepartureInstant)
    );
    flights.add(
        this.createFlight(routeORDtoSLC, unfilteredOriginDepartureInstant)
    );
    flights
        .add(this.createFlight(routeSLCtoORD, roundTripOriginDepartureInstant));
    flights
        .add(this.createFlight(routeORDtoGCK, roundTripOriginDepartureInstant));
    flights
        .add(this.createFlight(routeGCKtoSLC, roundTripOriginDepartureInstant));
    // routeLAXtoSFO = createRoute(airportLAX, airportSFO);
  }

  @Test
  void
      XmlInputForFlightOriginDestinationUnfilteredOriginDepartureZeroStops_GetNonStopFlight_ThenStatus200_XmlOutput_AssertValidFlightPathAndValidStops()
          throws Exception,
          JsonProcessingException
  {
    Integer stops = 0;
    String originIataId = "JFK";
    String destinationIataId = "SFO";
    String UNFILTERED_URI = String.format(
        URI,
        originIataId,
        destinationIataId,
        unfilteredOriginDepartureInstant.getEpochSecond()
    ) + stops;

    mvc.perform(
        get(UNFILTERED_URI).accept(MediaType.APPLICATION_XML)
            .contentType(MediaType.APPLICATION_XML)
    ).andExpect(status().isOk())
        .andExpect(
            content().contentTypeCompatibleWith(MediaType.APPLICATION_XML)
        )
        .andExpect(
            xpath("Set/item[1]/route/originAirport/iataId")
                .string(is(originIataId))
        )
        .andExpect(
            xpath("Set/item[last()]/route/destinationAirport/iataId")
                .string(is(destinationIataId))
        ).andExpect(
            xpath("count(Set/item) <= " + (stops + 1)).booleanValue(true)
        );
  }

  @Test
  void
      XmlInputForFlightOriginDestinationUnfilteredOriginDepartureOneHundredStops_GetConnectingFlightsWithin4Stops_ThenStatus200_XmlOutput_AssertValidFlightPathAndValidStops()
          throws Exception,
          JsonProcessingException
  {
    Integer stops = 100;
    String originIataId = "JFK";
    String destinationIataId = "SFO";
    String UNFILTERED_URI = String.format(
        URI,
        originIataId,
        destinationIataId,
        unfilteredOriginDepartureInstant.getEpochSecond()
    ) + stops;

    mvc.perform(
        get(UNFILTERED_URI).accept(MediaType.APPLICATION_XML)
            .contentType(MediaType.APPLICATION_XML)
    ).andExpect(status().isOk())
        .andExpect(
            content().contentTypeCompatibleWith(MediaType.APPLICATION_XML)
        )
        .andExpect(
            xpath("Set/item[1]/route/originAirport/iataId")
                .string(is(originIataId))
        )
        .andExpect(
            xpath("Set/item[last()]/route/destinationAirport/iataId")
                .string(is(destinationIataId))
        ).andExpect(
            xpath("count(Set/item) <= " + (stops + 1)).booleanValue(true)
        );
  }
}
