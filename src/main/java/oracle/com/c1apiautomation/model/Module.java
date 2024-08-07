package oracle.com.c1apiautomation.model;

import java.util.List;

public class Module extends SelectableBase {
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

    @Override
    public Module clone() {
        // TODO: copy mutable state here, so the clone can't change the internals of the original
        return (Module) super.clone();
    }
}
