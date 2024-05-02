import lombok.RequiredArgsConstructor;
import org.example.dao.RawDataDAO;
import org.example.entity.RawData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(classes = MainServiceImplTest.class)
public class MainServiceImplTest {
    @Autowired
    private RawDataDAO rawDataDAO;

    @Test
    public void testSaveRawData(){
        Update update = new Update();
        Message msg = new Message();
        msg.setText("lol");
        update.setMessage(msg);
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        Set<RawData> testData = new HashSet<>();
        testData.add(rawData);
        rawDataDAO.save(rawData);
        Assert.isTrue(testData.contains(rawData),"Entity not found in the set");
    }

}
