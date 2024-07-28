package oracle.com.c1apiautomation.model;

import java.util.List;

public class Module extends  SelectableBase{
//    private BooleanProperty selected  = new SimpleBooleanProperty();
    private String name;
    private List<PreReq> preReqs;
    private List<TestCase> testCases;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PreReq> getPreReqs() {
        return preReqs;
    }

    public void setPreReqs(List<PreReq> preReqs) {
        this.preReqs = preReqs;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

//    public boolean isSelected() {
//        return selected.get();
//    }
//
//    public BooleanProperty getSelected() {
//        return selected;
//    }
//
//    public void setSelected(Boolean selected) {
//        this.selected.set(selected);
//    }
}
