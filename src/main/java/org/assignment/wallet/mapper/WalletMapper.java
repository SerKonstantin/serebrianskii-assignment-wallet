package org.assignment.wallet.mapper;

import org.assignment.wallet.dto.WalletBalanceDTO;
import org.assignment.wallet.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class WalletMapper {
    @Mapping(target = "walletId", source = "id")
    public abstract WalletBalanceDTO map(Wallet wallet);
}
