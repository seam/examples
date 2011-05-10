package org.jboss.seam.test;

import javax.el.ELContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.steeplesoft.jsf.facestester.FacesTester;
import org.jboss.webbeans.context.RequestContext;
import org.jboss.webbeans.context.SessionContext;
import org.jboss.webbeans.context.api.BeanStore;
import org.jboss.webbeans.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.webbeans.environment.servlet.Listener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * A base class for scenarios that execute JSF pages
 * in a JCDI environment.
 * <p/>
 * TODO move me to a Seam module (perhaps the test module)
 *
 * @author Dan Allen
 */
public class AbstractScenario {
    protected FacesTester tester;

    protected Listener wbListener;

    @AfterClass(alwaysRun = true)
    public void afterClass() throws Exception {
        ServletContext sc = (ServletContext) tester.getFacesContext().getExternalContext().getContext();
        wbListener.contextDestroyed(new ServletContextEvent(sc));
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        SessionContext.instance().destroy();
        SessionContext.instance().setActive(false);
        RequestContext.instance().destroy();
        RequestContext.instance().setActive(false);
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        tester = FacesTesterHolder.instance();
        ServletContext sc = (ServletContext) tester.getFacesContext().getExternalContext().getContext();
        wbListener = new Listener();
        wbListener.contextInitialized(new ServletContextEvent(sc));
    }

    @BeforeMethod
    public void beforeMethod() {
        BeanStore reqBS = new ConcurrentHashMapBeanStore();
        RequestContext.instance().setBeanStore(reqBS);
        RequestContext.instance().setActive(true);
        BeanStore sessBS = new ConcurrentHashMapBeanStore();
        SessionContext.instance().setBeanStore(sessBS);
        SessionContext.instance().setActive(true);
    }

    protected <T> T getValue(String expression, Class<T> expectedClass) {
        if (!expression.startsWith("#{")) {
            expression = "#{" + expression + "}";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();
        return (T) context.getApplication().getExpressionFactory().createValueExpression(elContext, expression, expectedClass).getValue(elContext);
    }
}
