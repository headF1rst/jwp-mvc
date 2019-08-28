package core.mvc.asis;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.mvc.HandlerMapping;
import next.controller.LogoutController;

public class LegacyHandlerMapping implements HandlerMapping<Controller> {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);
    private Map<String, Controller> mappings = new HashMap<>();

    void initMapping() {
        
    	mappings.put("/users/logout", new LogoutController());
    	
        logger.info("Initialized Request Mapping!");
        mappings.keySet().forEach(path -> {
            logger.info("Path : {}, Controller : {}", path, mappings.get(path).getClass());
        });
    }

    public Controller findController(String url) {
        return mappings.get(url);
    }

    void put(String url, Controller controller) {
        mappings.put(url, controller);
    }
    
    @Override
	public boolean support(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return mappings.containsKey(requestUri);
	}

    @Override
    public Controller getHandler(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        logger.debug("Method : {}, Request URI : {}", request.getMethod(), requestUri);

        Controller controller = findController(requestUri);
        return controller;
    }
}