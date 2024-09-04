package oracle.com.c1apiautomation.model;

import java.util.ArrayList;
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
        Module copy = (Module) super.clone();

        // Deep copy the preReqs list
//        if (this.preReqs != null) {
//            copy.preReqs = new ArrayList<>(this.preReqs.size());
//            for (PreReq preReq : this.preReqs) {
//                copy.preReqs.add(preReq.clone()); // Ensure PreReq implements clone
//            }
//        }
//
//        // Deep copy the testCases list
//        if (this.testCases != null) {
//            copy.testCases = new ArrayList<>(this.testCases.size());
//            for (TestCase testCase : this.testCases) {
//                copy.testCases.add(testCase.clone()); // Ensure TestCase implements clone
//            }
//        }

        return copy;
    }
}
