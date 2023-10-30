package net.bc100dev.osintgram4j.pcl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class PCLCaller {

    private final String command, helpDesc;
    private Class callableClass;
    private Method callableMethod, callableHelpMethod;
    private final String[] alternateCommands;

    public PCLCaller(String command, String helpDesc, String callableClassName, String... alternateCommands)
            throws PCLException {
        this.command = command;
        this.helpDesc = helpDesc;
        this.alternateCommands = alternateCommands;

        this.callableClass = null;
        this.callableMethod = null;

        try {
            this.callableClass = Class.forName(callableClassName);

            Method[] callableMethodArr = this.callableClass.getDeclaredMethods();
            if (callableMethodArr.length == 0)
                throw new PCLException("No methods are given for the method");

            Method execMethod = this.callableClass.getDeclaredMethod("launchCmd", String[].class, List.class);
            Method helpMethod = this.callableClass.getDeclaredMethod("helpCmd", String[].class);

            if (execMethod.getReturnType() != int.class)
                throw new IllegalArgumentException("Execution Method does not return an Integer Value");

            this.callableMethod = execMethod;
            this.callableHelpMethod = helpMethod;
        } catch (ClassNotFoundException | NoSuchMethodException ignore) {
            throw new PCLException("No class or method found in class " + callableClassName);
        }
    }

    public String[] getAlternateCommands() {
        return alternateCommands;
    }

    public String getCommand() {
        return command;
    }

    public String retrieveShortHelp() {
        return helpDesc;
    }

    public int execute(String[] args, List<PCLConfig> configList) throws PCLException {
        if (callableMethod == null || callableClass == null)
            throw new NullPointerException("Method and/or Class not initialized");

        try {
            return (Integer) callableMethod.invoke(null, args, configList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PCLException(e);
        }
    }

    public String retrieveLongHelp(String[] args) throws PCLException {
        if (callableHelpMethod == null || callableClass == null)
            throw new NullPointerException("Help Method and/or Class not initialized properly");

        try {
            return (String) callableHelpMethod.invoke(String.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PCLException(e);
        }
    }

}
