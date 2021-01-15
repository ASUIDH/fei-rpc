import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private int id;
    private String message;
    public HelloObject(){}
}
