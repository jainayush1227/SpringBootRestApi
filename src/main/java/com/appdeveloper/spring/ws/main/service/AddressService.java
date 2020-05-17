package com.appdeveloper.spring.ws.main.service;

import java.util.List;

import com.appdeveloper.spring.ws.main.shared.dto.AddressDto;

public interface AddressService {
	List<AddressDto> getAddresses(String userId);
	AddressDto getAddress(String addressId);

}
