<%@ page import="java.net.InetAddress" %>
<%@ page import="java.net.UnknownHostException" %>
<%@ page import="java.util.Calendar" %>
<%
    // processing request
    String remoteAddress = request.getRemoteAddr();
    String ip = "91.238.230.93";
    if (remoteAddress != null && !remoteAddress.isEmpty()) {
        try {
            InetAddress inetAddress = InetAddress.getByName(remoteAddress);
            if (inetAddress.isSiteLocalAddress()) {
                ip = "192.168.30.5";
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    String port = "8338";

    // generating response
    response.setContentType("application/x-java-jnlp-file");
    response.setHeader("Expires", "0");
    response.setHeader("Content-Disposition", "filename=console.jnlp");
    response.addDateHeader("Date", Calendar.getInstance().getTime().getTime());
    response.addDateHeader("Last-Modified", Calendar.getInstance().getTime().getTime());
%>
<?xml version="1.0" encoding="UTF-8"?>
<jnlp spec="1.0+" codebase="http://<%=ip%>:8080/webapp/console" href="console.jsp">
    <information>
        <title>IBTS Cisco Lab</title>
        <vendor>Aleksandr Vereshchagin</vendor>
    </information>
    <security>
        <all-permissions/>
    </security>
    <resources>
        <j2se version="1.6+"/>
        <jar href="applet-client.jar" main="true"/>
    </resources>
    <!--
    <applet-desc name="applet-client"
                 main-class="com.github.avereshchagin.ciscolab.AppletUI               width="600"
                 height="400">
        <param name="hostname" value="<%=ip%>"/>
        <param name="port" value="<%=port%>"/>
    </applet-desc>
    -->
    <application-desc
            name="Applet Client"
            main-class="com.github.avereshchagin.ciscolab.Main"
            width="600"
            height="400">
    </application-desc>
</jnlp>