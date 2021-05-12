package com.smoothstack.utopia.flightplaneservice.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rob Maes
 * May 12 2021
 */
@Getter
@Setter
@ToString
public class CreateSeatLayoutDto {

  private List<CreateSeatGroupDto> seatGroups;
}
