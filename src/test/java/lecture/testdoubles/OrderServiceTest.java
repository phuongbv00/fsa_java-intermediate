package lecture.testdoubles;

import lecture.testdoubles.service.NotificationService;
import lecture.testdoubles.service.OrderRepository;
import lecture.testdoubles.service.OrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceTest {
    private static final Map<String, String> sharedState = new HashMap<>();
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeAll
    public static void beforeAll() {
        // setup test data
        // setup shared state
    }

    @AfterAll
    public static void afterAll() {
        sharedState.clear();
    }

    @Test
    public void getOrderStatus_HappyCase() {
        System.out.println(this);
        // stub orderRepository.getStatus(anyString()) -> 10
        Mockito.when(orderRepository.getStatus(Mockito.anyString())).thenReturn(10);
        Assertions.assertEquals(10, orderService.getOrderStatus("1", true));
        // verify interaction with orderRepository
        Mockito.verify(orderRepository, Mockito.times(1)).getStatus(Mockito.anyString());
    }

    @Test
    public void getOrderStatus_123() {
        System.out.println(this);
        // stub orderRepository.getStatus(anyString()) -> 10
        Mockito.when(orderRepository.getStatus("123")).thenReturn(10);
        Assertions.assertEquals(10, orderService.getOrderStatus("123", true));
        // verify interaction with orderRepository
        Mockito.verify(orderRepository, Mockito.times(1)).getStatus("123");
    }

    @RepeatedTest(value = 2)
    @Order(1)
    public void processOrder_ExistingOrderStatus() {
        sharedState.put("status", "1");
        System.out.println(this);
        String orderId = "123";
        Mockito.when(orderRepository.getStatus(orderId)).thenReturn(5).thenReturn(6);
        orderService.processOrder(orderId);
        Mockito.verify(orderRepository, Mockito.times(1)).updateStatus(orderId, 6);
        Mockito.verify(notificationService).send("Order " + orderId + " is processed with status " + 6);
    }

    @Test
    public void processOrder_NullOrderStatus() {
        sharedState.put("status", "1");
        System.out.println(this);
        String orderId = "123";
        Mockito.when(orderRepository.getStatus(orderId)).thenReturn(null).thenReturn(1);
        orderService.processOrder(orderId);
        Mockito.verify(orderRepository, Mockito.times(1)).updateStatus(orderId, 1);
        Mockito.verify(notificationService).send("Order " + orderId + " is processed with status " + 1);
    }

    static List<Arguments> provideProcessOrderArgs() {
        List<Arguments> args = new java.util.ArrayList<>(IntStream.range(1, 11).mapToObj(i -> Arguments.of(i, i + 1)).toList());
        args.add(Arguments.of(null, 1));
        return args;
    }

    @ParameterizedTest
//    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}) -> error
//    @CsvSource(value = {"null,1", "5,6"}, nullValues = {"null"}) -> OK
    @MethodSource("provideProcessOrderArgs")
    @Tag("integration")
    @DisplayName("processOrder with provideProcessOrderArgs")
    @Order(2)
    public void processOrder(Integer oldStatus, Integer newStatus) {
        System.out.println(sharedState.get("status"));
        System.out.println(this);
        String orderId = "123";
        Mockito.when(orderRepository.getStatus(orderId)).thenReturn(oldStatus).thenReturn(newStatus);
        orderService.processOrder(orderId);
        Mockito.verify(orderRepository, Mockito.times(1)).updateStatus(orderId, newStatus);
        Mockito.verify(notificationService).send("Order " + orderId + " is processed with status " + newStatus);
    }
}
