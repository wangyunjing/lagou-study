package com.lagou.pojo;

import com.lagou.sqlSession.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/8/3
 */
public class MapperMethod {
    SqlCommandType sqlCommandType;
    String statementId;
    Method method;
    boolean returnVoid = false;
    boolean returnInt = false;
    boolean returnLong = false;
    boolean returnBoolean = false;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        String className = mapperInterface.getName();
        String methodName = method.getName();
        statementId = className + "." + methodName;
        MappedStatement mappedStatement = config.getMappedStatementMap().get(statementId);
        sqlCommandType = mappedStatement.getSqlCommandType();
        Type returnType = method.getGenericReturnType();
        if (void.class.equals(returnType)) {
            returnVoid = true;
        } else if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
            returnInt = true;
        } else if (Long.class.equals(returnType) || Long.TYPE.equals(returnType)) { // Long
            returnLong = true;
        } else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) { // Boolean
            returnBoolean = true;
        }
    }

    public Object execute(SqlSession sqlSession, Object[] args) throws Exception {
        Object result = null;
        //判断mapper中的方法类型，最终调用的还是SqlSession中的方法
        switch (sqlCommandType) {
            case SELECT: {
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                // 判断是否进行了 泛型类型参数化
                if (genericReturnType instanceof ParameterizedType) {
                    List<Object> objects = sqlSession.selectList(statementId, args);
                    return objects;
                }
                return sqlSession.selectOne(statementId, args);
            }
            case UPDATE: {
                // 转换 rowCount
                result = rowCountResult(sqlSession.update(statementId, args));
                break;
            }
            default:
                throw new RuntimeException("Unknown execution method ");
        }
        return result;
    }

    private Object rowCountResult(int rowCount) {
        final Object result;
        if (returnVoid) { // Void 情况，不用返回
            result = null;
        } else if (returnInt) { // Int
            result = rowCount;
        } else if (returnLong) { // Long
            result = (long) rowCount;
        } else if (returnBoolean) { // Boolean
            result = rowCount > 0;
        } else {
            throw new RuntimeException();
        }
        return result;
    }
}
