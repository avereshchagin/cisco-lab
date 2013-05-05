package com.github.avereshchagin.ciscolab.webapp;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@ManagedBean(name = "lanDetector")
public class LanDetector {

    private static final String LOCAL_RACK_ADDRESS = "192.168.30.5";
    private static final String REMOTE_RACK_ADDRESS = "91.238.230.93";

    public String getRackAddress() {
        HttpServletRequest request =
                (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request != null) {
            String address = request.getRemoteAddr();
            if (address != null && !address.isEmpty()) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(address);
                    if (inetAddress.isSiteLocalAddress()) {
                        return LOCAL_RACK_ADDRESS;
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        return REMOTE_RACK_ADDRESS;
    }
}
