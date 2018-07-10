import com.app.LocalDateTimeConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class ConfigurationTest {

    private LocalDateTimeConverter c = new LocalDateTimeConverter();

    @Test
    public void givenValidLdtShouldReturnCorrectTimestamp() {
        LocalDateTime ldt;
        Timestamp ts;

        ldt = LocalDateTime.now();
        ts =  Timestamp.valueOf(ldt.withNano(0));

        assertThat(c.convertToDatabaseColumn(ldt)).isEqualTo(ts);
        assertThat(c.convertToDatabaseColumn(null)).isEqualTo(null);
    }

    @Test
    public void givenNotValidLdtShouldReturnNull() {
        assertThat(c.convertToDatabaseColumn(null)).isEqualTo(null);
    }

    @Test
    public void givenValidTimestampShouldReturnLdt() {
        LocalDateTime ldt = LocalDateTime.now();
        Timestamp ts =Timestamp.valueOf(ldt);

        assertThat(c.convertToEntityAttribute(ts)).isEqualTo(ldt);
        assertThat(c.convertToEntityAttribute(null)).isEqualTo(null);
    }

    @Test
    public void givenNotValidTimestampShouldReturnNull() {
        assertThat(c.convertToEntityAttribute(null)).isEqualTo(null);
    }
}

