package oracle.com.c1apiautomation.model;

import java.io.Closeable;
import java.util.List;

public class Microservice extends  SelectableBase {

    private String name;
    private List<Module> modules;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public Microservice clone() {
        // TODO: copy mutable state here, so the clone can't change the internals of the original
        return (Microservice) super.clone();
    }
}
