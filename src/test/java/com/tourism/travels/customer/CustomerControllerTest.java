package com.tourism.travels.customer;

import com.tourism.travels.exception.GlobalExceptionHandler;
import com.tourism.travels.pojo.CustomerDetailsResource;
import com.tourism.travels.pojo.CustomerSignUp;
import com.tourism.travels.sql.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private TravelMapper travelMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        var customerController = new CustomerController(customerService, travelMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class GetCustomers {

        @Test
        void works() throws Exception {
            // Arrange
            var customerEntity = new CustomerEntity();
            var customerDetailsResource = getCustomerDetailsResource();

            when(customerService.getCustomerDetails()).thenReturn(Collections.singletonList(customerEntity));
            when(travelMapper.toCustomerDetailsResource(customerEntity)).thenReturn(customerDetailsResource);

            // Act/Assert
            mockMvc.perform(get("/customers"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(CUSTOMER_DETAILS_RESPONSE));

            verify(customerService).getCustomerDetails();
            verify(travelMapper).toCustomerDetailsResource(customerEntity);

            verifyNoMoreInteractions(customerService, travelMapper);
        }

    }

    @Nested
    class GetCustomerById {

        @Test
        void works() throws Exception {
            // Arrange
            var customerId = 123;
            var customerEntity = new CustomerEntity();
            var request = CUSTOMER_DETAILS_RESPONSE.replace("[", "");
            var customerDetailsResource = getCustomerDetailsResource();

            when(customerService.getCustomerEntityById(customerId)).thenReturn(customerEntity);
            when(travelMapper.toCustomerDetailsResource(customerEntity)).thenReturn(customerDetailsResource);

            // Act/Assert
            mockMvc.perform(get("/customers/123"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(request));

            verify(customerService).getCustomerEntityById(customerId);
            verify(travelMapper).toCustomerDetailsResource(customerEntity);

            verifyNoMoreInteractions(customerService, travelMapper);
        }

    }

    @Nested
    class SignUpCustomer {

        @Test
        void works() throws Exception {
            // Arrange
            var customerSignUp = new CustomerSignUp();
            customerSignUp.setCustomerId(123);
            customerSignUp.setFirstName("firstName");
            customerSignUp.setLastName("lastName");
            customerSignUp.setEmail("email@gmail.com");
            customerSignUp.setPassword("secret");

            var customerEntity = new CustomerEntity();

            when(travelMapper.toCustomerEntity(any(CustomerSignUp.class))).thenReturn(customerEntity);
            when(customerService.signUp(customerEntity)).thenReturn(customerEntity);
            when(travelMapper.toSignUpRequest(customerEntity)).thenReturn(customerSignUp);

            // Act/Assert
            mockMvc.perform(put("/customers/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(CUSTOMER_SIGN_UP))
                    .andExpect(status().isOk())
                    .andExpect(content().json(CUSTOMER_SIGN_UP));

            verify(travelMapper).toCustomerEntity(any(CustomerSignUp.class));
            verify(customerService).signUp(customerEntity);
            verify(travelMapper).toSignUpRequest(customerEntity);

            verifyNoMoreInteractions(customerService, travelMapper);
        }

        @Test
        void return400BadException_whenCustomerIdIsNull() throws Exception {
            // Arrange
            var request = CUSTOMER_SIGN_UP.replace("123", "null");
            var errorMessage = COMMON_ERROR_MESSAGE.replace("fieldName", "customerId").replace("empty", "null");

            // Act/Assert
            mockMvc.perform(put("/customers/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(errorMessage));
        }

        @Test
        void return400BadException_whenFirstNameIsEmpty() throws Exception {
            // Arrange
            var request = CUSTOMER_SIGN_UP.replace("firstName", "");
            var errorMessage = COMMON_ERROR_MESSAGE.replace("fieldName", "firstName");

            // Act/Assert
            mockMvc.perform(put("/customers/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(errorMessage));
        }

        @Test
        void return400BadException_whenEmailIsNotInProperForm() throws Exception {
            // Arrange
            var request = CUSTOMER_SIGN_UP.replace("email@gmail.com", "email");
            var errorMessage = COMMON_ERROR_MESSAGE.replace("fieldName", "email")
                    .replace("must not be empty", "must be a well-formed email address");

            // Act/Assert
            mockMvc.perform(put("/customers/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(errorMessage));
        }

        @Test
        void return400BadException_whenEmailIsEmpty() throws Exception {
            // Arrange
            var request = CUSTOMER_SIGN_UP.replace("email@gmail.com", "");
            var errorMessage = COMMON_ERROR_MESSAGE.replace("fieldName", "email");

            // Act/Assert
            mockMvc.perform(put("/customers/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(errorMessage));
        }

        @Test
        void return400BadException_whenPasswordIsEmpty() throws Exception {
            // Arrange
            var request = CUSTOMER_SIGN_UP.replace("secret", "");
            var errorMessage = COMMON_ERROR_MESSAGE.replace("fieldName", "password");

            // Act/Assert
            mockMvc.perform(put("/customers/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(errorMessage));
        }

    }

    @Nested
    class DeleteCustomer {

        @Test
        void works() throws Exception {
            // Act/Assert
            mockMvc.perform(delete("/customers/1"))
                    .andExpect(status().isNoContent());

            verify(customerService).deleteByCustomerId(1);

            verifyNoMoreInteractions(customerService);
        }

    }

    private CustomerDetailsResource getCustomerDetailsResource() {

        var customerDetailsResource = new CustomerDetailsResource();
        customerDetailsResource.setCustomerId(123);
        customerDetailsResource.setFirstName("firstName");
        customerDetailsResource.setLastName("lastName");
        customerDetailsResource.setEmail("email@gmail.com");
        customerDetailsResource.setPassword("password");

        return customerDetailsResource;
    }

    private static final String CUSTOMER_DETAILS_RESPONSE =
            """
                    [
                        {
                            "customerId": 123,
                            "firstName": "firstName",
                            "lastName": "lastName",
                            "email": "email@gmail.com",
                            "password": "password"
                        }
                    ]""";

    private static final String CUSTOMER_SIGN_UP =
            """
                    {
                        "customerId": 123,
                        "firstName": "firstName",
                        "lastName": "lastName",
                        "email": "email@gmail.com",
                        "password": "secret"
                    }""";

    private static final String COMMON_ERROR_MESSAGE =
            """
                    [
                        {
                            "field": "fieldName",
                            "message": "must not be empty"
                        }
                    ]""";

}
