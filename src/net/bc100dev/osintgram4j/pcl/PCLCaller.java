package net.bc100dev.osintgram4j.pcl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class PCLCaller {

    private final String command, helpDesc;
    private final String[] args;
    private Class callableClass;
    private Method callableMethod, callableHelpMethod;

    public PCLCaller(String command, String helpDesc, String[] args, String callableClassName)
            throws PCLException {
        this.command = command;
        this.helpDesc = helpDesc;
        this.args = args;

        this.callableClass = null;
        this.callableMethod = null;

        try {
            this.callableClass = Class.forName(callableClassName);

            Method[] callableMethodArr = this.callableClass.getDeclaredMethods();
            if (callableMethodArr.length == 0)
                throw new PCLException("No methods are given for the method");

            Method execMethod = this.callableClass.getDeclaredMethod("launchCmd", String[].class, String[].class);
            Method helpMethod = this.callableClass.getDeclaredMethod("helpCmd");

            this.callableMethod = execMethod;
            this.callableHelpMethod = helpMethod;
        } catch (ClassNotFoundException | NoSuchMethodException ignore) {
            throw new PCLException("No class or method found in class " + callableClassName);
        }
    }

    public String retrieveShortHelp() {
        return helpDesc;
    }

    public void execute(String[] givenReqArgs, String[] givenOptArgs, List<PCLConfig> configList) throws PCLException {
        if (callableMethod == null || callableClass == null)
            throw new NullPointerException("Method and/or Class not initialized");

        try {
            callableMethod.invoke(null, givenReqArgs, givenOptArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PCLException(e);
        }
    }

    public String retrieveLongHelp() throws PCLException {
        if (callableHelpMethod == null || callableClass == null)
            throw new NullPointerException("Help Method and/or Class not initialized");

        try {
            return (String) callableHelpMethod.invoke(String.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PCLException(e);
        }
    }

}
