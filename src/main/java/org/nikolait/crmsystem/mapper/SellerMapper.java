package org.nikolait.crmsystem.mapper;

import org.mapstruct.*;
import org.nikolait.crmsystem.dto.SellerCreateRequest;
import org.nikolait.crmsystem.dto.SellerResponse;
import org.nikolait.crmsystem.dto.SellerUpdateRequest;
import org.nikolait.crmsystem.model.Seller;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SellerMapper {

    SellerResponse toResponse(Seller seller);

    Seller toEntity(SellerCreateRequest request);

    void patchFromRequest(SellerUpdateRequest request, @MappingTarget Seller seller);
}
