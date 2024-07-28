package oracle.com.c1apiautomation.model;

import java.util.List;

public class Microservice extends  SelectableBase{
//    private final BooleanProperty selected  = new SimpleBooleanProperty();
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

//    public boolean isSelected() {
//        return selected.get();
//    }
//
//    public BooleanProperty  getSelected() {
//        return selected;
//    }
//
//    public void setSelected(boolean  selected) {
//        this.selected.set(selected);
//    }
}
