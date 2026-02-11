package org.ecommerce.paymentservice.mapper;


import org.ecommerce.paymentservice.dto.FinishedPaymentEvent;
import org.ecommerce.paymentservice.dto.StartPaymentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    FinishedPaymentEvent toFinishedEvent(StartPaymentEvent startPaymentEvent);
}
