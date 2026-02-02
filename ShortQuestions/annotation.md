## Java Spring Common Annotations

### Core Spring / Spring Boot
- `@SpringBootApplication` – Entry point of Spring Boot application
- `@Component` – Marks a Spring-managed component
- `@Service` – Service-layer component (business logic)
- `@Repository` – DAO layer, enables exception translation
- `@Autowired` – Dependency Injection

### Web / REST
- `@RestController` – Combines @Controller and @ResponseBody for REST APIs
- `@Controller` – Marks a class as Spring MVC controller
- `@RequestMapping` – Maps HTTP requests to handler methods (can specify path, method, etc.)
- `@GetMapping` – Maps HTTP GET requests
- `@PostMapping` – Maps HTTP POST requests
- `@PutMapping` – Maps HTTP PUT requests
- `@DeleteMapping` – Maps HTTP DELETE requests
- `@PatchMapping` – Maps HTTP PATCH requests
- `@PathVariable` – Binds URL template variable to method parameter
- `@RequestParam` – Binds HTTP request parameter to method parameter
- `@RequestBody` – Binds HTTP request body to method parameter (JSON/XML)
- `@ResponseBody` – Indicates return value should be written to HTTP response body
- `@ResponseStatus` – Specifies HTTP status code for response
- `@CrossOrigin` – Enables CORS for specific handler methods/controllers

### JPA / Hibernate
- `@Entity` – Marks a class as JPA entity (database table)
- `@Table` – Specifies table name and schema
- `@Id` – Marks primary key field
- `@GeneratedValue` – Specifies primary key generation strategy
- `@Column` – Maps field to database column (name, nullable, length, etc.)
- `@OneToMany` – Defines one-to-many relationship
- `@ManyToOne` – Defines many-to-one relationship
- `@ManyToMany` – Defines many-to-many relationship
- `@OneToOne` – Defines one-to-one relationship
- `@JoinColumn` – Specifies foreign key column name
- `@JoinTable` – Specifies join table for many-to-many relationships
- `@Temporal` – Maps temporal types (DATE, TIME, TIMESTAMP)
- `@Enumerated` – Maps enum types
- `@Lob` – Maps large objects (BLOB/CLOB)
- `@Transient` – Marks field as non-persistent

### Repository / Spring Data JPA
- `@Repository` – Marks DAO layer, enables exception translation
- `@Query` – Defines custom query (JPQL or native SQL)
- `@Modifying` – Indicates query modifies data (use with @Query)
- `@Transactional` – Defines transaction boundaries
- `@Param` – Binds method parameter to query parameter

### Exception Handling
- `@ControllerAdvice` – Global exception handler for all controllers
- `@ExceptionHandler` – Handles specific exceptions in controller/advice
- `@ResponseStatus` – Specifies HTTP status for exception response

### Validation
- `@Valid` – Triggers validation on method parameter/field
- `@Validated` – Group-based validation
- `@NotNull` – Field must not be null
- `@NotEmpty` – Field must not be null or empty
- `@NotBlank` – String must not be blank (null, empty, or whitespace)
- `@Size` – Validates size (min/max) of collection/string/array
- `@Min` / `@Max` – Validates numeric min/max values
- `@Email` – Validates email format
- `@Pattern` – Validates string against regex pattern
- `@Past` / `@Future` – Validates date is in past/future

### Bean Configuration / IoC
- `@Configuration` – Marks class as Spring configuration class
- `@Bean` – Defines a Spring bean (method-level)
- `@ComponentScan` – Configures component scanning base packages
- `@PropertySource` – Loads properties file
- `@Value` – Injects property value or expression
- `@Profile` – Conditionally activates beans based on active profile
- `@ConditionalOnProperty` – Conditionally creates bean based on property
- `@Primary` – Marks bean as primary when multiple candidates exist
- `@Qualifier` – Specifies which bean to inject when multiple candidates exist

### Dependency Injection
- `@Autowired` – Dependency injection (by type, can use with @Qualifier)
- `@Required` – Field must be injected (deprecated, use constructor injection)
- `@Inject` – JSR-330 standard dependency injection (alternative to @Autowired)
- `@Resource` – JSR-250 standard dependency injection (by name)

### Spring MVC – Controller / View
- `@ModelAttribute` – Binds method parameter/return value to model
- `@SessionAttributes` – Specifies session attributes for controller
- `@RequestHeader` – Binds HTTP header to method parameter
- `@CookieValue` – Binds cookie value to method parameter
- `@InitBinder` – Customizes data binding
- `@ControllerAdvice` – Shared behavior across controllers

### Spring Security
- `@EnableWebSecurity` – Enables Spring Security web security support
- `@PreAuthorize` – Method-level security (checks before method execution)
- `@PostAuthorize` – Method-level security (checks after method execution)
- `@Secured` – Method-level security (role-based)
- `@RolesAllowed` – JSR-250 security annotation
- `@EnableGlobalMethodSecurity` – Enables method-level security