# JUnit Tests for Spring Boot Services

## Overview
This package contains comprehensive JUnit test suites for all service classes in the Spring Boot application. The tests use Mockito for mocking dependencies and follow best practices for unit testing.

## Test Files Created

### 1. AuthServiceTest.java
Tests authentication functionality including:
- Login with valid credentials
- JWT cookie generation and attributes
- Logout functionality
- Authentication manager integration

### 2. CategoryServiceTest.java
Tests category management including:
- Get all categories
- Add new category
- Update existing category
- Delete category
- User authorization checks

### 3. CompanyCreditServiceTest.java
Tests company credit operations:
- Get all credits
- Get credit by company profile ID
- Handle non-existent credits

### 4. CompanyProfileServiceTest.java
Tests company profile management:
- Register new company with user and credit
- Update company profile with authorization
- Delete company profile and related entities
- Get company details with full information
- Calculate average reviews

### 5. CreditTransactionServiceTest.java
Tests credit transaction operations:
- Get all transactions
- Get transactions by company
- Refund credit with various validations
- Add credit to user
- Admin transaction queries

### 6. EmailServiceTest.java
Tests email sending functionality:
- Send email with correct details
- Handle multiline bodies
- Handle special characters
- Handle empty and long bodies

### 7. JwtServiceTest.java
Tests JWT token operations:
- Generate valid tokens
- Extract username from token
- Validate tokens
- Extract claims
- Handle token expiration
- Generate tokens with extra claims

### 8. NewUserDetailsServiceTest.java
Tests user details loading for authentication:
- Load user by username
- Handle non-existent users
- User authorities and account status
- Handle special characters in username

### 9. SkillsServiceTest.java
Tests skills management:
- CRUD operations for skills
- Assign skills to companies
- Remove skills from companies
- Search skills by keyword
- Get skills by company

### 10. UserServiceTest.java
Tests user management:
- Get all users
- Add new user with password hashing
- Update user information
- Delete user
- Proper password encryption validation

## Dependencies Required

Add these dependencies to your `pom.xml`:

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=CompanyProfileServiceTest
```

### Run with Coverage
```bash
mvn test jacoco:report
```

## Test Structure

Each test class follows this structure:

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @BeforeEach
    void setUp() {
        // Initialize test data
    }
    
    @Test
    void testMethod_ShouldDoSomething_WhenCondition() {
        // Arrange
        // Act
        // Assert
    }
}
```

## Key Testing Patterns Used

### 1. Mocking Dependencies
```java
@Mock
private UserRepository userRepository;

when(userRepository.findById(1)).thenReturn(Optional.of(user));
```

### 2. Verifying Interactions
```java
verify(repository, times(1)).save(any(Entity.class));
verify(repository, never()).delete(any());
```

### 3. Testing Exceptions
```java
ApiException exception = assertThrows(ApiException.class,
    () -> service.methodThatThrows());
assertEquals("Expected message", exception.getMessage());
```

### 4. Argument Captors
```java
ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
verify(repository).save(userCaptor.capture());
User savedUser = userCaptor.getValue();
```

### 5. Password Hashing Verification
```java
assertTrue(new BCryptPasswordEncoder().matches("password", 
    savedUser.getPassword()));
```

## Test Coverage

The test suites cover:
- ✅ Happy path scenarios
- ✅ Error handling and exceptions
- ✅ Edge cases (null values, empty lists)
- ✅ Authorization checks
- ✅ Data validation
- ✅ Repository interactions
- ✅ DTO conversions

## Common Assertions Used

```java
// Null checks
assertNotNull(result);
assertNull(result);

// Equality
assertEquals(expected, actual);
assertNotEquals(value1, value2);

// Boolean
assertTrue(condition);
assertFalse(condition);

// Collections
assertTrue(list.isEmpty());
assertEquals(2, list.size());
```

## Best Practices Implemented

1. **Test Isolation**: Each test is independent and doesn't rely on other tests
2. **Clear Naming**: Test names follow the pattern `methodName_ShouldExpectedBehavior_WhenCondition`
3. **Arrange-Act-Assert**: Tests follow the AAA pattern for clarity
4. **Mock Only External Dependencies**: Only repositories and external services are mocked
5. **Comprehensive Coverage**: Both success and failure scenarios are tested
6. **Meaningful Assertions**: Multiple assertions verify different aspects of the result

## Additional Services to Test

The following services still need test coverage (you can create similar tests):
- ContractAgreementService
- ServiceRequestService
- ServiceBidService
- ReviewService
- TicketService
- PaymentService
- SubscriptionService

## Troubleshooting

### Common Issues

1. **NullPointerException in tests**
   - Ensure all mocks are properly initialized with `@Mock`
   - Check that `@InjectMocks` is used on the service under test

2. **Verification failures**
   - Use `verify()` with correct argument matchers
   - Check if the method was actually called in the test

3. **BCryptPasswordEncoder issues**
   - Don't compare encoded passwords with `equals()`
   - Use `BCryptPasswordEncoder.matches()` instead

## Contributing

When adding new tests:
1. Follow the existing naming conventions
2. Use `@BeforeEach` for common setup
3. Test both success and failure scenarios
4. Include edge cases
5. Add comments for complex test logic

## License

This test suite is part of the Manfaa project.
