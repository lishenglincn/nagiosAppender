/*
The MIT License (MIT)
Copyright (c) 2012 Sebastian Schuth

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*/
package de.viaboxx.nagios;

import com.googlecode.jsendnsca.*;
import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;
import com.googlecode.jsendnsca.builders.NagiosSettingsBuilder;
import com.googlecode.jsendnsca.encryption.Encryption;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.MDC;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Appender sending error logs via nagios.
 */
public class NagiosAppender extends AppenderSkeleton {

    public static final String MONITORED_SERVICE_NAME = "monitoredServiceName";
    public static final String MONITORED_HOST_NAME = "monitoredHostName";

    private NagiosPassiveCheckSender sender;

    private String nagiosServiceName, nagiosHost, nagiosEncryption, nagiosPassword;

    private int nagiosPort = 5667; //5667 is default

    private String monitoredHostName, monitoredServiceName;

    private String monitorWarnAs = "WARNING", monitorErrorAs = "CRITICAL", monitorFatalAs = "CRITICAL";

    private boolean resetStateAfterCritical = false;
    private boolean resetStateAfterWarning = false;

    @Override
    protected void append(LoggingEvent event) {
        Level nagiosLevel = nagiosLevel(event);
        if (nagiosLevel != null) {
            sendEventWithLevel(renderMsg(event), nagiosLevel);
            if (resetStateAfterCritical && nagiosLevel.equals(Level.CRITICAL)) {
                sendEventWithLevel("Reset after CRITICAL: " + event.getRenderedMessage(), Level.OK);
            }
            if (resetStateAfterWarning && nagiosLevel.equals(Level.WARNING)) {
                sendEventWithLevel("Reset after WARNING: " + event.getRenderedMessage(), Level.OK);
            }
        }
    }

    private String renderMsg(LoggingEvent event) {
        return "[" + event.getLoggerName() + "] " + event.getRenderedMessage();
    }

    private void sendEventWithLevel(String message, Level nagiosLevel) {
        MessagePayload payload = new MessagePayloadBuilder()
                .withHostname(findHostName())
                .withServiceName(findServiceName())
                .withLevel(nagiosLevel)
                .withMessage(message)
                .create();
        try {
            sender.send(payload);
        } catch (NagiosException e) {
            LogLog.error("Nagios Exception=" + e.getMessage());
        } catch (IOException e) {
            LogLog.error("IOException=" + e.getMessage());
        }
    }

    private Level nagiosLevel(LoggingEvent event) {
        if (event.getLevel().equals(org.apache.log4j.Level.WARN)) {
            return Level.valueOf(monitorWarnAs);
        }
        if (event.getLevel().equals(org.apache.log4j.Level.ERROR)) {
            return Level.valueOf(monitorErrorAs);
        }
        if (event.getLevel().equals(org.apache.log4j.Level.FATAL)) {
            return Level.valueOf(monitorFatalAs);
        }
        return null;
    }

    private String findServiceName() {
        String serviceName;
        String serviceNameFromMDC = (String) MDC.get(MONITORED_SERVICE_NAME);
        if (monitoredServiceName != null && !monitoredServiceName.isEmpty()) {
            serviceName = monitoredServiceName;
        } else if (serviceNameFromMDC != null && !serviceNameFromMDC.isEmpty()) {
            serviceName = serviceNameFromMDC;
        } else {
            serviceName = "SERVICE_NAME_NOT_SET";
        }
        return serviceName;
    }

    public void close() {
        // nothing to do (?)
    }

    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void activateOptions() {
        Encryption encryption = parseEncryption();
        NagiosSettings nagiosSettings = new NagiosSettingsBuilder()
                .withNagiosHost(nagiosHost)
                .withPort(nagiosPort)
                .withEncryption(encryption)
                .withPassword(nagiosPassword)
                .create();
        sender = new NagiosPassiveCheckSender(nagiosSettings);
    }

    private String findHostName() {
        String hostName;
        String hostNameFromMDC = (String) MDC.get(MONITORED_HOST_NAME);
        if (monitoredHostName != null && !monitoredHostName.isEmpty()) {
            hostName = monitoredHostName;
        } else if (hostNameFromMDC != null && !hostNameFromMDC.isEmpty()) {
            hostName = hostNameFromMDC;
        } else {
            try {
                hostName = monitoredHostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                hostName = "UNKNOWN_HOST_AND_NOT_CONFIGURED";
                LogLog.error(e.getMessage());
            }
        }
        return hostName;
    }

    private Encryption parseEncryption() {
        Encryption encryption;
        try {
            encryption = Encryption.valueOf(nagiosEncryption);
        } catch (IllegalArgumentException illegalArgument) {
            LogLog.error("Unknown nagios encryption: " + nagiosEncryption + ". Using NONE by default.");
            encryption = Encryption.NONE;
        }
        return encryption;
    }

    // GETTERS/SETTERS FROM HERE ON

    public NagiosPassiveCheckSender getSender() {
        return sender;
    }

    public void setSender(NagiosPassiveCheckSender sender) {
        this.sender = sender;
    }

    public String getNagiosServiceName() {
        return nagiosServiceName;
    }

    public void setNagiosServiceName(String nagiosServiceName) {
        this.nagiosServiceName = nagiosServiceName;
    }

    public String getNagiosHost() {
        return nagiosHost;
    }

    public void setNagiosHost(String nagiosHost) {
        this.nagiosHost = nagiosHost;
    }

    public int getNagiosPort() {
        return nagiosPort;
    }

    public void setNagiosPort(int nagiosPort) {
        this.nagiosPort = nagiosPort;
    }

    public String getNagiosEncryption() {
        return nagiosEncryption;
    }

    public void setNagiosEncryption(String nagiosEncryption) {
        this.nagiosEncryption = nagiosEncryption;
    }

    public String getNagiosPassword() {
        return nagiosPassword;
    }

    public void setNagiosPassword(String nagiosPassword) {
        this.nagiosPassword = nagiosPassword;
    }

    public String getMonitoredServiceName() {
        return monitoredServiceName;
    }

    public void setMonitoredServiceName(String monitoredServiceName) {
        this.monitoredServiceName = monitoredServiceName;
    }

    public String getMonitoredHostName() {
        return monitoredHostName;
    }

    public void setMonitoredHostName(String monitoredHostName) {
        this.monitoredHostName = monitoredHostName;
    }

    public String getMonitorWarnAs() {
        return monitorWarnAs;
    }

    public void setMonitorWarnAs(String monitorWarnAs) {
        this.monitorWarnAs = monitorWarnAs;
    }

    public String getMonitorErrorAs() {
        return monitorErrorAs;
    }

    public void setMonitorErrorAs(String monitorErrorAs) {
        this.monitorErrorAs = monitorErrorAs;
    }

    public String getMonitorFatalAs() {
        return monitorFatalAs;
    }

    public void setMonitorFatalAs(String monitorFatalAs) {
        this.monitorFatalAs = monitorFatalAs;
    }

    public boolean isResetStateAfterCritical() {
        return resetStateAfterCritical;
    }

    public void setResetStateAfterCritical(boolean resetStateAfterCritical) {
        this.resetStateAfterCritical = resetStateAfterCritical;
    }

    public boolean isResetStateAfterWarning() {
        return resetStateAfterWarning;
    }

    public void setResetStateAfterWarning(boolean resetStateAfterWarning) {
        this.resetStateAfterWarning = resetStateAfterWarning;
    }
}
