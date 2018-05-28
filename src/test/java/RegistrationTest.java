import com.app.currencies.Currency;
import com.app.currencies.CurrencyRepository;
import com.app.registrations.Registration;
import com.app.registrations.RegistrationController;
import com.app.registrations.RegistrationRepository;
import com.app.tariffs.Tariff;
import com.app.tariffs.TariffRepository;
import com.app.vip.VipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
public class RegistrationTest {

    private MockMvc mockMvc;
    private RegistrationRepository registrationRepository;
    private TariffRepository tariffRepository;
    private RegistrationController controller;
    private VipRepository vipRepository;
    private CurrencyRepository currencyRepository;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        currencyRepository = mock(CurrencyRepository.class);
        registrationRepository = mock(RegistrationRepository.class);
        tariffRepository = mock(TariffRepository.class);
        vipRepository = mock(VipRepository.class);
        controller = new RegistrationController(registrationRepository, tariffRepository, vipRepository, currencyRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    //not-Rest tests
    @Test
    public void givenRegistrationWithoutDbShouldHaveNullId() {
        Registration registration = new Registration();
        assertThat(registration.getId()).isNull();
    }

    @Test
    public void givenRegistrationShouldBeAbleToSetDeparture() {
        Registration registration = new Registration();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currTime = formatter.format(LocalDateTime.now());
        registration.setDeparture(currTime);
        assertThat(registration.getDeparture()).isEqualTo(LocalDateTime.parse(currTime, formatter));
    }

    @Test
    public void givenRegistrationAllMembersShouldInitializeCorrectly() {
        //given
        Registration registration = new Registration();
        LocalDateTime currTime = LocalDateTime.now();

        //when
        registration.setRegistrationPlate("test123");
        registration.setArrival(currTime);
        registration.setTariffId(1L);
        registration.setDeparture(currTime);

        //then
        assertThat(registration).isNotEqualTo(null);
        assertThat(registration.getRegistrationPlate()).isEqualTo("test123");
        assertThat(registration.getArrival()).isEqualTo(currTime);
        assertThat(registration.getTariffId()).isEqualTo(1L);
        assertThat(registration.getDeparture()).isEqualTo(currTime);
    }


    @Test
    public void givenDepartureAndArrivalShouldCalculatePriceCorrectlyOutsideBasicPeriod() throws Exception {
        //given
        Tariff testTariff = mock(Tariff.class);
        Registration testRegistration = new Registration();
        LocalDateTime currTime = LocalDateTime.now();
        Currency c = new Currency();
        c.setRate(1.0);
        c.setCode("PLN");
        //when
        when(testTariff.getFirst()).thenReturn(new BigDecimal("1.0"));
        when(testTariff.getSecond()).thenReturn(new BigDecimal("2.0"));
        when(testTariff.getNext()).thenReturn(1.5);
        testRegistration.setArrival(currTime);
        testRegistration.setDeparture(currTime.plusHours(2));
        when(currencyRepository.findTopByCodeEquals("PLN")).thenReturn(Optional.of(c));

        //then
        double price = Whitebox.<BigDecimal> invokeMethod(controller, "calculatePrice", testRegistration, testTariff, LocalDateTime.now().plusHours(2),"PLN").doubleValue();
        assertThat(price).isEqualTo(3.0);
    }

    @Test
    public void givenDepartureAndArrivalTimeShouldCalculatePriceCorrectlyInsideBasicPeriod() throws Exception {
        //given
        Registration testRegistration = new Registration();
        Tariff testTariff = mock(Tariff.class);
        LocalDateTime currTime = LocalDateTime.now();
        Currency c = new Currency();
        c.setRate(1.0);
        c.setCode("PLN");
        //when
        when(testTariff.getFirst()).thenReturn(new BigDecimal("1.0"));
        when(testTariff.getSecond()).thenReturn(new BigDecimal("2.0"));
        when(testTariff.getNext()).thenReturn(1.5);
        testRegistration.setArrival(currTime);
        testRegistration.setDeparture(currTime.plusMinutes(30));
        when(currencyRepository.findTopByCodeEquals("PLN")).thenReturn(Optional.of(c));
        //then
        double price = Whitebox.<BigDecimal>invokeMethod(controller, "calculatePrice", testRegistration, testTariff, LocalDateTime.now().plusHours(1),"PLN").doubleValue();
        assertThat(price).isEqualTo(1.0);

    }

    @Test
    public void givenNonVipRegistrationPlateShouldReturnRegistration()throws Exception {
        //given
        Tariff t = new Tariff();
        //when
        String registrationPlate = "WAW12345";
        when(vipRepository.existsByRegistrationPlate(registrationPlate)).thenReturn(false);
        when(tariffRepository.findTopByVipEqualsOrderByIdDesc(false)).thenReturn(t);
        //then
        Registration result = Whitebox.<Registration>invokeMethod(controller, "createRegistration", registrationPlate);
        assertThat(registrationPlate).isEqualTo(result.getRegistrationPlate());
    }

    @Test
    public void givenVipRegistrationPlateShouldReturnRegistration()throws Exception {
        //given
        Tariff t = new Tariff();
        //when
        String registrationPlate = "WAW12345";
        when(vipRepository.existsByRegistrationPlate(registrationPlate)).thenReturn(true);
        when(tariffRepository.findTopByVipEqualsOrderByIdDesc(true)).thenReturn(t);
        //then
        Registration result = Whitebox.<Registration>invokeMethod(controller, "createRegistration", registrationPlate);
        assertThat(registrationPlate).isEqualTo(result.getRegistrationPlate());
    }

    //Rest POST tests
    @Test
    public void postCheckoutGivenExistingRegistrationPlateShouldCheckout()throws Exception {
        //given
        String correctPlate = "WAW123";

        Tariff t = new Tariff();
        t.setFirst(new BigDecimal(1.0));
        t.setSecond(new BigDecimal(2.0));
        t.setNext(1.2);

        Currency c = new Currency();
        c.setRate(1.0);
        c.setCode("PLN");

        Registration reg = new Registration();
        reg.setRegistrationPlate(correctPlate);
        reg.setArrival(LocalDateTime.now().minusHours(3));
        reg.setTariffId((long)1);

        Registration request = new Registration();
        request.setRegistrationPlate(correctPlate);

        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc(correctPlate)).thenReturn(Optional.of(reg));
        when(tariffRepository.findOne((long)1)).thenReturn(Optional.of(t));
        when(currencyRepository.findTopByCodeEquals("PLN")).thenReturn(Optional.of(c));
        MockHttpServletResponse response = mockMvc.perform(post("/checkout")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW123\", \"currency\": \"PLN\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        CheckoutResponse res = mapper.readValue(response.getContentAsString(), CheckoutResponse.class);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(res.getRegistrationPlate()).isEqualTo(correctPlate);
        assertThat(res.getFee()).isEqualTo(5.4);
    }

    @Test
    public void postCheckoutGivenNotExistingRegistrationPlateShouldSendNotFoundStatus()throws Exception {
        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("BI123")).thenReturn(Optional.empty());

        MockHttpServletResponse response = mockMvc.perform(post("/checkout")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"BI123\", \"currency\": \"PLN\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void postUnregisterGivenNotExistingEntryShouldSendNotFound()throws Exception {
        //when
        when(registrationRepository.findTopByRegistrationPlateOrderByArrivalDesc("WAW121")).thenReturn(Optional.empty());
        MockHttpServletResponse response = mockMvc.perform(post("/unregister")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW121\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void postUnregisterGivenExistingEntryShouldUnregister()throws Exception {
        //given
        Registration reg=new Registration();
        reg.setRegistrationPlate("WAW120");
        reg.setTariffId((long)1);
        reg.setArrival(LocalDateTime.now().minusHours(3));

        //when
        when(registrationRepository.findTopByRegistrationPlateOrderByArrivalDesc("WAW120")).thenReturn(Optional.of(reg));
        MockHttpServletResponse response = mockMvc.perform(post("/unregister")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW120\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        BDDMockito.verify(registrationRepository).save(Mockito.any(Registration.class));
    }

    @Test
    public void postRegisterGivenTooLongPlateShouldSendNotAcceptable()throws Exception {
        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("WAW1234567890")).thenReturn(Optional.empty());

        MockHttpServletResponse response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW1234567890\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void postRegisterGivenAlreadyExistingEntryShouldSendBadRequest()throws Exception {
        //given
        Registration reg = new Registration();
        reg.setRegistrationPlate("WAW120");
        reg.setTariffId((long) 1);
        reg.setArrival(LocalDateTime.now().minusHours(3));

        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("WAW120")).thenReturn(Optional.of(reg));

        MockHttpServletResponse response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW120\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void postRegisterGivenNotAlreadyExistingEntryShouldRegister()throws Exception {
        //given
        Tariff t = new Tariff();
        t.setFirst(new BigDecimal(1.0));
        t.setSecond(new BigDecimal(2.0));
        t.setNext(1.2);

        //when
        when(registrationRepository.findTopByRegistrationPlateAndDepartureIsNullOrderByArrivalDesc("WAW121")).thenReturn(Optional.empty());
        when(tariffRepository.findTopByVipEqualsOrderByIdDesc(false)).thenReturn(t);

        MockHttpServletResponse response = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"registrationPlate\":\"WAW121\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        BDDMockito.verify(registrationRepository).save(Mockito.any(Registration.class));
    }
}

class CheckoutResponse {
    private Double fee;
    private String registrationPlate;
    private String currency;
    private LocalDateTime arrival;
    private LocalDateTime departure;
    private Long tariffId;
    public Double getFee() { return fee; }
    public void setFee(Double fee) {
        this.fee = fee;
    }
    public String getRegistrationPlate() {
        return registrationPlate;
    }
    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public void setArrival(String arrival) {
        arrival=arrival.replace('T',' ');
        arrival=arrival.substring(0,arrival.length()-4);
        this.arrival = LocalDateTime.parse(arrival, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public void setDeparture(String departure) {
        departure=departure.replace('T',' ');
        departure=departure.substring(0,departure.length()-4);
        this.departure = LocalDateTime.parse(departure, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
