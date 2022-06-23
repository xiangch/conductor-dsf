package cn.com.do1.conductor.client.spring;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zengxc
 */
public class ConductorHttpMessageConverter extends FastJsonHttpMessageConverter {

    private static final String conductorPackage = "com.netflix.conductor";


    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        if (type.getTypeName().startsWith(conductorPackage)) {
            return super.canRead(type, contextClass, mediaType);
        }
        return false;

    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        if (type.getTypeName().startsWith(conductorPackage)) {
            return super.canWrite(type, clazz, mediaType);
        }
        return false;
    }


}
