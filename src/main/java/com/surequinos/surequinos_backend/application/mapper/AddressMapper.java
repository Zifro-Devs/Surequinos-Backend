package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.AddressDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateAddressRequest;
import com.surequinos.surequinos_backend.domain.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre Address entity y DTOs
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address toEntity(CreateAddressRequest request);

    AddressDto toDto(Address address);
}

