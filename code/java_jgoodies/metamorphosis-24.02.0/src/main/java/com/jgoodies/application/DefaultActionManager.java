package com.jgoodies.application;

import com.jgoodies.application.internal.ActionAnnotationUtils;
import com.jgoodies.application.internal.ActionMethods;
import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.jsdl.internal.CommonFormats;
import java.awt.event.ActionEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultActionManager.class */
public class DefaultActionManager implements ActionManager {
    private static final String INVOCATION_FAILED_FORMAT = "Invocation of an annotated method failed.\nClass=%1$s\nObject=%2$s\nMethod=%3$s%4$s";

    @Override // com.jgoodies.application.ActionManager
    public ActionMap createActionMap(Object target, ResourceMap resourceMap) {
        ApplicationActionMap map = new ApplicationActionMap(this, target, resourceMap);
        Map<String, ActionMethods.MethodOrException> methods = ActionMethods.getInstance().getMethods(target.getClass());
        if (methods == null) {
            throw new Exceptions.ListenerMethodNotFoundException(String.format("No public method annotated with @Action found.\nTarget class=%1$s", target.getClass().getName()));
        }
        methods.forEach((id, methodOrException) -> {
            methodOrException.throwIfException();
            Method method = methodOrException.getMethod();
            Action annotation = (Action) method.getAnnotation(Action.class);
            map.addAction(method, id, annotation);
        });
        return map;
    }

    protected javax.swing.Action createAction(Object target, Method method, String actionId) {
        return new DefaultApplicationAction(target, method);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultActionManager$ApplicationActionMap.class */
    public static final class ApplicationActionMap extends ActionMap {
        private final DefaultActionManager actionManager;
        private final Object target;
        private final ResourceMap targetResourceMap;
        private final ResourceMap customResourceMap;

        ApplicationActionMap(DefaultActionManager actionManager, Object target, ResourceMap customResourceMap) {
            this.actionManager = actionManager;
            this.target = target;
            this.targetResourceMap = Application.getResourceMap(target.getClass());
            this.customResourceMap = customResourceMap;
        }

        void addAction(Method method, String actionId, Action annotation) {
            ResourceMap resourceMap;
            javax.swing.Action action = this.actionManager.createAction(this.target, method, actionId);
            if (ActionAnnotationUtils.providesResources(annotation)) {
                ActionAnnotationUtils.configureAction(action, actionId, annotation, this.target, method);
            } else {
                Class<?> targetClass = this.target.getClass();
                Class<?> declaringClass = method.getDeclaringClass();
                boolean hasCustomResourceMap = this.customResourceMap != null;
                if (hasCustomResourceMap) {
                    resourceMap = this.customResourceMap;
                } else if (declaringClass == targetClass) {
                    resourceMap = this.targetResourceMap;
                } else {
                    resourceMap = Application.getResourceMap(declaringClass);
                }
                ActionAnnotationUtils.configureAction(action, actionId, annotation, this.target, method, resourceMap, hasCustomResourceMap);
            }
            super.put(actionId, action);
        }

        public javax.swing.Action get(Object key) {
            javax.swing.Action result = super.get(key);
            if (result == null) {
                failNotFound(key);
            }
            return result;
        }

        public void clear() {
            throw new UnsupportedOperationException("Clearing an ApplicationActionMap is prohibited.");
        }

        public void put(Object key, javax.swing.Action action) {
            throw new UnsupportedOperationException("Putting actions to an ApplicationActionMap is prohibited.");
        }

        public void remove(Object key) {
            throw new UnsupportedOperationException("Removing actions from an ApplicationActionMap is prohibited.");
        }

        private void failNotFound(Object key) {
            String message = String.format("\nTarget class =%1$s\nAction id    =\"%2$s\"\nAvailable ids=%3$s", this.target.getClass().getName(), key, availableActionIds());
            throw new Exceptions.ActionNotFoundException(message);
        }

        private String availableActionIds() {
            return (String) Stream.of(keys()).map((v0) -> {
                return v0.toString();
            }).collect(Collectors.joining(CommonFormats.COMMA_DELIMITER));
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/DefaultActionManager$DefaultApplicationAction.class */
    public static class DefaultApplicationAction extends AbstractAction {
        private final Object target;
        private final Method method;

        protected DefaultApplicationAction(Object target, Method method) {
            this.target = target;
            this.method = method;
        }

        protected Object getTarget() {
            return this.target;
        }

        public Method getMethod() {
            return this.method;
        }

        public void actionPerformed(ActionEvent evt) {
            Preconditions.checkState(isEnabled(), "You must not perform disabled Actions.\nTarget class=%1$s\nMethod name =%2$s", getTarget().getClass().getName(), getMethod().getName());
            invokeMethod(getTarget(), getMethod(), evt);
        }

        private static Object invokeMethod(Object target, Method method, ActionEvent evt) {
            try {
                Object[] arguments = method.getParameterTypes().length == 0 ? null : new Object[]{evt};
                return method.invoke(target, arguments);
            } catch (IllegalAccessException e1) {
                throw new RuntimeException(methodInvocationFailureString(target, method), e1);
            } catch (IllegalArgumentException e12) {
                throw new RuntimeException(methodInvocationFailureString(target, method), e12);
            } catch (InvocationTargetException e13) {
                Throwable cause = e13.getCause();
                if (cause instanceof Error) {
                    throw ((Error) cause);
                }
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                }
                throw new RuntimeException(methodInvocationFailureString(target, method), cause);
            }
        }

        private static String methodInvocationFailureString(Object target, Method method) {
            Class<?> targetClass = target != null ? target.getClass() : null;
            StringBuilder builder = new StringBuilder();
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                builder.append('@');
                builder.append(annotation.annotationType().getSimpleName());
                builder.append(' ');
            }
            return String.format(DefaultActionManager.INVOCATION_FAILED_FORMAT, targetClass, target, builder.toString(), method);
        }
    }
}
