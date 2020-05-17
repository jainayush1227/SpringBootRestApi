package com.appdeveloper.spring.ws.main.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appdeveloper.spring.ws.main.entity.AddressEntity;
import com.appdeveloper.spring.ws.main.entity.UserEntity;
import com.appdeveloper.spring.ws.main.repository.AddressRepository;
import com.appdeveloper.spring.ws.main.repository.UserRepository;
import com.appdeveloper.spring.ws.main.service.AddressService;
import com.appdeveloper.spring.ws.main.shared.dto.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnValue = new ArrayList<>();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) {
			return returnValue;
		}
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		for (AddressEntity addressEntity : addresses) {
			returnValue.add(new ModelMapper().map(addressEntity, AddressDto.class));
		}
		return returnValue;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		AddressDto returnValue = null;
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if (addressEntity != null) {
			return new ModelMapper().map(addressEntity, AddressDto.class);
		}

		return returnValue;
	}

}
