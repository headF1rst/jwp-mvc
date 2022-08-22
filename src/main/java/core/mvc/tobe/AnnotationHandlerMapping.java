package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMapping;
import core.annotation.web.RequestMethod;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class AnnotationHandlerMapping extends AbstractHandlerMapping {

    private final Object[] basePackage;
    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public void initialize() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller : controllers) {
            String path = controller.getAnnotation(Controller.class).path();
            detectHandlerExecution(path, controller);
        }
    }

    private void detectHandlerExecution(String path, Class<?> controller) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Object controllerInstance = controller.getConstructor().newInstance();
        List<Method> methods = List.of(controller.getDeclaredMethods());
        for (Method method : methods) {
            addHandlerExecution(controllerInstance, path, method);
        }
    }

    private void addHandlerExecution(Object controllerInstance, String path, Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            String uriPath = method.getAnnotation(RequestMapping.class).value();
            RequestMethod requestMethod = method.getAnnotation(RequestMapping.class).method();
            HandlerKey handlerKey = new HandlerKey(path + uriPath, requestMethod);

            logger.debug("Add RequestMapping. URI: {}, requestMethod: {}", handlerKey, method);
            handlerExecutions.put(handlerKey, new HandlerExecution(controllerInstance, method));
        }
    }
}
