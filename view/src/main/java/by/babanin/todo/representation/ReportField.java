package by.babanin.todo.representation;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import by.babanin.todo.model.ReportableField;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.Translator;

public class ReportField {

    private final Field field;

    public ReportField(Field field) {
        this.field = field;
    }

    public int getIndex() {
        return getReportableField().index();
    }

    public boolean isMandatory() {
        return getReportableField().mandatory();
    }

    private ReportableField getReportableField() {
        return field.getAnnotation(ReportableField.class);
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public String getCaption() {
        return Translator.getFieldCaption(this);
    }

    public Object getValue(Object o) {
        try {
            return getReadMethod().invoke(o);
        }
        catch(IllegalAccessException | InvocationTargetException e) {
            throw new ViewException(e);
        }
    }

    private Method getReadMethod() {
        try {
            String name = getName();
            for (PropertyDescriptor pd : Introspector.getBeanInfo(getDeclaringClass()).getPropertyDescriptors()) {
                if(pd.getReadMethod() != null && name.equals(pd.getName())) {
                    return pd.getReadMethod();
                }
            }
            throw new ViewException(name + " field getter isn't exist");
        }
        catch(IntrospectionException e) {
            throw new ViewException(e);
        }
    }
}
