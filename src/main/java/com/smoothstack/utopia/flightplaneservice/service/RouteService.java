package com.smoothstack.utopia.flightplaneservice.service;

import com.smoothstack.utopia.flightplaneservice.dao.RouteDao;
import com.smoothstack.utopia.flightplaneservice.dto.CreateRouteDto;
import com.smoothstack.utopia.flightplaneservice.dto.UpdateRouteDto;
import com.smoothstack.utopia.flightplaneservice.exception.RouteNotFoundException;
import com.smoothstack.utopia.shared.model.Route;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Rob Maes
 * Mar 17 2021
 */
@Service
public class RouteService {

  private final RouteDao routeDao;

  @Autowired
  public RouteService(RouteDao routeDao) {
    this.routeDao = routeDao;
  }

  public List<Route> getAllRoutes() {
    return routeDao.findAll();
  }

  public Route getRoute(Long routeId) {
    return routeDao.findById(routeId).orElseThrow(RouteNotFoundException::new);
  }

  public void createRoute(CreateRouteDto createRouteDto) {
    //TODO: implement createRoute
  }

  public void updateRoute(Long routeId, UpdateRouteDto updateRouteDto) {
    //TODO: implement updateRoute
  }

  public void deleteRoute(Long routeId) {
    //TODO: implement deleteRoute
  }
}
