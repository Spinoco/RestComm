package org.mobicents.servlet.restcomm.rvd.model.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mobicents.servlet.restcomm.rvd.BuildService;
import org.mobicents.servlet.restcomm.rvd.RvdUtils;
import org.mobicents.servlet.restcomm.rvd.exceptions.InterpreterException;
import org.mobicents.servlet.restcomm.rvd.interpreter.Interpreter;
import org.mobicents.servlet.restcomm.rvd.model.rcml.RcmlSmsStep;

public class SmsStep extends Step {
    static final Logger logger = Logger.getLogger(BuildService.class.getName());
    String text;
    String to;
    String from;
    String statusCallback;
    String method;
    String next;
    public String getNext() {
        return next;
    }
    public void setNext(String next) {
        this.next = next;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getStatusCallback() {
        return statusCallback;
    }
    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }
    public RcmlSmsStep render(Interpreter interpreter) {
        RcmlSmsStep rcmlStep = new RcmlSmsStep();

        if ( ! RvdUtils.isEmpty(getNext()) ) {
            String newtarget = interpreter.getTarget().getNodename() + "." + getName() + ".actionhandler";
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", newtarget);
            String action = interpreter.buildAction(pairs);
            rcmlStep.setAction(action);
            rcmlStep.setMethod(getMethod());
        }

        rcmlStep.setFrom(getFrom());
        rcmlStep.setTo(getTo());
        rcmlStep.setStatusCallback(getStatusCallback());
        rcmlStep.setText(interpreter.populateVariables(getText()));

        return rcmlStep;
    }
    public void handleAction(Interpreter interpreter) throws InterpreterException, IOException {
        logger.debug("handling sms action");
        if ( RvdUtils.isEmpty(getNext()) )
            throw new InterpreterException( "'next' module is not defined for step " + getName() );

        String SmsSid = interpreter.getHttpRequest().getParameter("SmsSid");
        String SmsStatus = interpreter.getHttpRequest().getParameter("SmsStatus");

        if ( SmsSid != null )
            interpreter.getVariables().put("SmsSid", SmsSid);
        if (SmsStatus != null )
            interpreter.getVariables().put("SmsStatus", SmsStatus);

        interpreter.interpret( getNext(), null );
    }
}