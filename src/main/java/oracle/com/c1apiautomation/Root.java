
package oracle.com.c1apiautomation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {
    private Microservice microservice;

    // Getters and Setters
    public Microservice getMicroservice() {
        return microservice;
    }

    public void setMicroservice(Microservice microservice) {
        this.microservice = microservice;
    }
}