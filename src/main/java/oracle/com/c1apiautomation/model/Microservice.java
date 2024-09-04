package oracle.com.c1apiautomation.model;

import java.io.Closeable;
import java.util.ArrayList;
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
        Microservice copy = (Microservice) super.clone();
//        copy.setSelected(this.isSelected()); // Clone the BooleanProperty value
//        // Deep copy the mutable fields
//        copy.name = this.name;  // Strings are immutable, so this is safe
//        if (this.modules != null) {
//            copy.modules = new ArrayList<>(this.modules.size());
//            for (Module module : this.modules) {
//                copy.modules.add(module.clone()); // Assuming Module also implements Cloneable
//            }
//        }
        return copy;
    }
}
