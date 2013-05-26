package com.github.avereshchagin.ciscolab.webapp.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(UserBean.class);
        classes.add(RackResource.class);
        classes.add(DeviceResource.class);
        classes.add(MenuResource.class);
        classes.add(JnlpResource.class);
        classes.add(LabResource.class);
        return classes;
    }
}
