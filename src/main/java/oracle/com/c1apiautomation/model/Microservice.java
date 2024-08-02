package oracle.com.c1apiautomation.model;

import java.util.List;

public class Microservice extends  SelectableBase{

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

}
