package com.appdeveloper.spring.ws.main.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdeveloper.spring.ws.main.exception.UserServiceException;
import com.appdeveloper.spring.ws.main.service.AddressService;
import com.appdeveloper.spring.ws.main.service.UserService;
import com.appdeveloper.spring.ws.main.shared.dto.AddressDto;
import com.appdeveloper.spring.ws.main.shared.dto.UserDto;
import com.appdeveloper.spring.ws.main.ui.model.request.UserDetailsRequestModel;
import com.appdeveloper.spring.ws.main.ui.model.response.AddressesRest;
import com.appdeveloper.spring.ws.main.ui.model.response.ErrorMessages;
import com.appdeveloper.spring.ws.main.ui.model.response.OperationStatusModel;
import com.appdeveloper.spring.ws.main.ui.model.response.RequestOperationName;
import com.appdeveloper.spring.ws.main.ui.model.response.RequestOperationStatus;
import com.appdeveloper.spring.ws.main.ui.model.response.UserRest;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	AddressService addressService;

	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String userId) {
		UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(userId);
		// BeanUtils.copyProperties(userDto, returnValue);
		ModelMapper mapper = new ModelMapper();
		returnValue = mapper.map(userDto, UserRest.class);
		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })

	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		/*
		 * UserDto userDto = new UserDto(); BeanUtils.copyProperties(userDetails,
		 * userDto);
		 */
		ModelMapper modelMapper = new ModelMapper();
		// modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		// BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		return returnValue;

	}

	@PutMapping(path = "/{userId}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		UserDto updatedUser = userService.updateUser(userId, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		return returnValue;
	}

	@DeleteMapping(path = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String userId) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(userId);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> usersList = userService.getUsers(page, limit);
		for (UserDto userDto : usersList) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}

		return returnValue;

	}

	// http:localhost:8080/user/{id}/addresses
	@GetMapping(path = "/{userId}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resources<AddressesRest> getUserAddresses(@PathVariable String userId) {
		List<AddressesRest> addressRestRequestModel = new ArrayList<>();
		List<AddressDto> addressDto = addressService.getAddresses(userId);
		if (addressDto != null && !addressDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
			ModelMapper modelMapper = new ModelMapper();
			addressRestRequestModel = modelMapper.map(addressDto, listType);
			for (AddressesRest addressRest : addressRestRequestModel) {
				Link addressLink = linkTo(
						methodOn(UserController.class).getUserAddress(userId, addressRest.getAddressId()))
								.withSelfRel();
				;
				Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");

				addressRest.add(addressLink);
				addressRest.add(userLink);
			}
		}

		return new Resources<>(addressRestRequestModel);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDto addressDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();

		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

		AddressesRest addressRestModel = modelMapper.map(addressDto, AddressesRest.class);
		addressRestModel.add(addressLink);
		addressRestModel.add(userLink);
		addressRestModel.add(addressesLink);
		return new Resource<AddressesRest>(addressRestModel);

	}
	/*
	 * http://localhost/mobile-app-ws/user/email-verification?token=sfddf
	 */

	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
		OperationStatusModel returnvalue = new OperationStatusModel();
		returnvalue.setOperationName(RequestOperationName.VERIFY_NAME.name());
		boolean isVerified = userService.verifyEmailToken(token);
		if (isVerified) {
			returnvalue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnvalue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		return returnvalue;

	}

}
