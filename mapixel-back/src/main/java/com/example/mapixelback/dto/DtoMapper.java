package com.example.mapixelback.dto;

import com.example.mapixelback.model.User;
import com.example.mapixelback.services.MapService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@NoArgsConstructor
@Component
public class DtoMapper {
    public UserSummaryWithMapsDto userToDtoWithMaps(User user, MapService mapService){
        UserSummaryWithMapsDto userDto = new UserSummaryWithMapsDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        List<String> foundMapsIds = user.getMaps();
        List<MapSummaryDto> foundMapsSummaries = foundMapsIds.stream().map(mapService::mapToSummaryDto).toList();
        userDto.setMaps(foundMapsSummaries);
        return userDto;
    }

}
