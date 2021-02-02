import annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ByeServiceImpl implements ByeService {
    private static final Logger logger= LoggerFactory.getLogger(ByeServiceImpl.class);
    @Override
    public String bye(String message) {
        logger.info("收到message :{}",message);
        return "byebye"+message;
    }
}
