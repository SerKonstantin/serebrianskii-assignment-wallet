package org.assignment.wallet.mapper;

import org.assignment.wallet.dto.TransactionRequestDTO;
import org.assignment.wallet.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { ReferenceMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TransactionMapper {
    @Mapping(target = "wallet", source = "walletId")
    public abstract Transaction map(TransactionRequestDTO transactionRequestDTO);
}
