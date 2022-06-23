package cn.com.do1.conductor.client.spring;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;

import java.lang.reflect.Type;


/**
 * conductor 专属 Converter。避免污染其它业务。
 *
 * @author zengxc
 */
public class ConductorHttpMessageConverter extends FastJsonHttpMessageConverter {

    private static final String conductorPackage = "com.netflix.conductor";


    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return type.getTypeName().startsWith(conductorPackage) ? super.canRead(type, contextClass, mediaType) : false;

    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return type.getTypeName().startsWith(conductorPackage) ? super.canWrite(type, clazz, mediaType) : false;
    }


}
