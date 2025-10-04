package paint.model;

import java.util.HashMap;
import java.util.Map;

public class PrototypeRegistry {
    private Map<String, Shape> prototypes = new HashMap<>();

    public void registerPrototype(String type, Shape prototype) {
        prototypes.put(type, prototype);
    }

    public Shape getPrototype(String type) throws CloneNotSupportedException {
        Shape prototype = prototypes.get(type);
        if (prototype != null) {
            return (Shape) prototype.clone();
        }
        return null;
    }
}
