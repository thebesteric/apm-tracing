package io.github.thebesteric.framework.apm.agent.commons.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class MethodInfo implements Serializable {

    private String methodName;
    private String returnType;
    private LinkedHashMap<String, Object> signatures = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> arguments = new LinkedHashMap<>();

    // 需要简单处理的相关类型
    protected static final List<Class<?>> SIMPLE_PROCESS_CLASSES = new ArrayList<>();

    static {
        SIMPLE_PROCESS_CLASSES.add(ServletRequest.class);
        SIMPLE_PROCESS_CLASSES.add(ServletResponse.class);
        SIMPLE_PROCESS_CLASSES.add(File.class);
        SIMPLE_PROCESS_CLASSES.add(InputStream.class);
        SIMPLE_PROCESS_CLASSES.add(OutputStream.class);
    }

    public MethodInfo() {
        super();
    }

    public MethodInfo(Method method, Object[] args) {
        this.methodName = method.getName();
        Parameter[] params = method.getParameters();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                Parameter param = params[i];
                // Add signatures
                signatures.put(param.getName(), param.getParameterizedType().getTypeName());

                // Processing args
                if (args != null) {
                    if (shouldSimpleProcess(param)) {
                        arguments.put(param.getName(), param.getParameterizedType().getTypeName());
                    } else {
                        if (args[i] instanceof Exception) {
                            arguments.put(param.getName(), String.valueOf(args[i]));
                        } else if (args[i] instanceof MultipartFile) {
                            MultipartFile file = (MultipartFile) args[i];
                            arguments.put(param.getName(), file.getOriginalFilename());
                        } else {
                            arguments.put(param.getName(), args[i]);
                        }
                    }
                }
            }
        }
        this.returnType = method.getReturnType().getName();
    }

    // 添加需要简单处理的相关类型
    public static void addSimpleProcessClass(Class<?> clazz) {
        SIMPLE_PROCESS_CLASSES.add(clazz);
    }

    // 是否需要简单处理
    private boolean shouldSimpleProcess(Parameter param) {
        for (Class<?> clazz : SIMPLE_PROCESS_CLASSES) {
            if (clazz.isAssignableFrom(param.getType())) return true;
        }
        return false;
    }
}