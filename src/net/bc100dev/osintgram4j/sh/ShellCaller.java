package net.bc100dev.osintgram4j.sh;

import osintgram4j.commons.ShellConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ShellCaller {

    private final String command, helpDesc;
    private Class<?> callableClass;
    private Method callableMethod, callableHelpMethod;
    private final String[] alternateCommands;

    private Object classInstance;

    public ShellCaller(String command, String helpDesc, String callableClassName, String... alternateCommands)
            throws ShellException {
        this.command = command;
        this.helpDesc = helpDesc;
        this.alternateCommands = alternateCommands;

        this.callableClass = null;
        this.callableMethod = null;

        try {
            this.callableClass = Class.forName(callableClassName);
            this.classInstance = callableClass.getDeclaredConstructor().newInstance();

            Method[] callableMethodArr = this.callableClass.getDeclaredMethods();
            if (callableMethodArr.length == 0)
                throw new ShellException("No methods are given for the method");

            Method execMethod = this.callableClass.getDeclaredMethod("launchCmd", String[].class, List.class);
            Method helpMethod = this.callableClass.getDeclaredMethod("helpCmd", String[].class);

            if (execMethod.getReturnType() != int.class)
                throw new IllegalArgumentException("Execution Method does not return an Integer Value");

            if (helpMethod.getReturnType() != String.class)
                throw new IllegalArgumentException("Help Method does not return a String value");

            this.callableMethod = execMethod;
            this.callableHelpMethod = helpMethod;
        } catch (ClassNotFoundException ignore) {
            throw new ShellException("No such class found at " + callableClassName);
        } catch (NoSuchMethodException ignore) {
            throw new ShellException("Class at " + callableClassName + " does not have the necessary methods");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
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

    public int execute(String[] args, List<ShellConfig> configList) throws ShellException {
        if (callableMethod == null || callableClass == null)
            throw new NullPointerException("Method and/or Class not initialized");

        try {
            return (Integer) callableMethod.invoke(classInstance, args, configList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ShellException(e);
        }
    }

    public String retrieveLongHelp(String[] args) throws ShellException {
        if (callableHelpMethod == null || callableClass == null)
            throw new NullPointerException("Help Method and/or Class not initialized properly");

        try {
            return (String) callableHelpMethod.invoke(classInstance, (Object) args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ShellException(e);
        }
    }

}
