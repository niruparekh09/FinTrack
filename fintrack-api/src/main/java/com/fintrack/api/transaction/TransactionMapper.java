package com.fintrack.api.transaction;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Transaction toTransaction(TransactionRequest request, Long userId);

    TransactionResponse toTransactionResponse(Transaction transaction);

    // Update existing entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateTransactionFromRequest(
            TransactionRequest request,
            @MappingTarget Transaction transaction
    );

}
