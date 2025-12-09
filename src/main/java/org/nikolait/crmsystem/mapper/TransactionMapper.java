package org.nikolait.crmsystem.mapper;

import org.mapstruct.*;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
import org.nikolait.crmsystem.model.Transaction;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {

    @Mapping(source = "seller.id", target = "sellerId")
    TransactionResponse toResponse(Transaction transaction);

    Transaction ToEntity(TransactionCreateRequest request);
}
