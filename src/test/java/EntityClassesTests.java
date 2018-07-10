import com.app.LocalDateTimeConverter;
import com.app.currencies.Currency;
import com.app.tariffs.Tariff;
import com.app.vip.Vip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class EntityClassesTests {

    private LocalDateTimeConverter c = new LocalDateTimeConverter();

    @Test
    public void checkVipClassMethods() {
        Vip vip = new Vip();
        vip.setRegistrationPlate("123");

        assertThat(vip.getRegistrationPlate()).isEqualTo("123");
        assertThat(vip.getId()).isNull();
    }

    @Test
    public void checkTariffClassMethods() {
        Tariff tariff = new Tariff();
        tariff.setFirst(new BigDecimal(2.0));
        tariff.setSecond(new BigDecimal(3.0));
        tariff.setNext(1.2);
        tariff.setVip(false);

        assertThat(tariff.getFirst()).isEqualTo(new BigDecimal(2.0));
        assertThat(tariff.getSecond()).isEqualTo(new BigDecimal(3.0));
        assertThat(tariff.getNext()).isEqualTo(1.2);
        assertThat(tariff.getVip()).isEqualTo(false);
        assertThat(tariff.getId()).isNull();
    }

    @Test
    public void checkCurrencyClassMethods() {
        Currency currency = new Currency();
        currency.setCode("EUR");
        currency.setRate(0.24);

        assertThat(currency.getCode()).isEqualTo("EUR");
        assertThat(currency.getRate()).isEqualTo(0.24);
        assertThat(currency.getId()).isNull();
    }
}

