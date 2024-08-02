
package oracle.com.c1apiautomation.model;

import java.util.List;

public class Root {

    private List<Environment> environments;
    private List<Microservice> microservices;

    public List<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public List<Microservice> getMicroservices() {
        return microservices;
    }

    public void setMicroservices(List<Microservice> microservices) {
        this.microservices = microservices;
    }

//    private Microservice microservice;
//
//    // Getters and Setters
//    public Microservice getMicroservice() {
//        return microservice;
//    }
//
//    public void setMicroservice(Microservice microservice) {
//        this.microservice = microservice;
//    }
}