package com.appdeveloper.spring.ws.main.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdeveloper.spring.ws.main.entity.UserEntity;
import com.appdeveloper.spring.ws.main.exception.UserServiceException;
import com.appdeveloper.spring.ws.main.helper.Utils;
import com.appdeveloper.spring.ws.main.repository.UserRepository;
import com.appdeveloper.spring.ws.main.service.UserService;
import com.appdeveloper.spring.ws.main.shared.dto.AddressDto;
import com.appdeveloper.spring.ws.main.shared.dto.UserDto;
import com.appdeveloper.spring.ws.main.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;
	@Autowired
	Utils util;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		if (repository.findByEmail(user.getEmail()) != null) {
			throw new RuntimeException("User Record Already Exist");
		}
		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDto address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(util.generateAddressId(30));
			user.getAddresses().set(i, address);
		}

		// UserEntity userEntity = new UserEntity();
		ModelMapper mapper = new ModelMapper();
		UserEntity userEntity = mapper.map(user, UserEntity.class);
		// BeanUtils.copyProperties(user, userEntity);
		String publicUserId = util.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(util.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);

		UserEntity userCreated = repository.save(userEntity);
		// UserDto returnValue = new UserDto();
		// BeanUtils.copyProperties(userCreated, returnValue);
		UserDto returnValue = mapper.map(userCreated, UserDto.class);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = repository.findByEmail(email);
		if (userEntity == null) {
			throw new UsernameNotFoundException("User is not avaibale");
		}

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
		// new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new
		// ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = repository.findByEmail(email);
		if (userEntity == null) {
			throw new UsernameNotFoundException("User is not avaibale");
		}
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = repository.findByUserId(userId);
		if (userEntity == null) {
			throw new UsernameNotFoundException(userId);
		}
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = repository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedUser = repository.save(userEntity);
		BeanUtils.copyProperties(updatedUser, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = repository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		repository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		if (page > 0) {
			page = page - 1;
		}
		Pageable pageable = PageRequest.of(page, limit);
		Page<UserEntity> userPage = repository.findAll(pageable);
		List<UserEntity> users = userPage.getContent();
		for (UserEntity user : users) {
			UserDto userdto = new UserDto();
			BeanUtils.copyProperties(user, userdto);
			returnValue.add(userdto);
		}

		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false;
		UserEntity userEntity = repository.findUserByEmailVerificationToken(token);
		if (userEntity != null) {
			boolean hastokenExpired = Utils.hasTokenExpired(token);
			if (!hastokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				repository.save(userEntity);
				returnValue = true;
			}
		}

		return returnValue;
	}

}
