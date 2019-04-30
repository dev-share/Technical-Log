 package com.hollysys.smartfactory.equipmentdiagnosis.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,RowBounds.class, ResultHandler.class})
})
public class QueryExecutorInterceptor implements Interceptor {

 private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
 private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
 private static final ReflectorFactory DEFAULT_OBJECT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
 private static final String ROOT_SQL_NODE = "sqlSource.rootSqlNode";


 @Override
 public Object intercept(Invocation invocation) throws Throwable {
	 Object parameter = invocation.getArgs()[1];
     MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
     MetaObject metaMappedStatement = MetaObject.forObject(statement, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_OBJECT_REFLECTOR_FACTORY);
     BoundSql boundSql = statement.getBoundSql(parameter);
     if (metaMappedStatement.hasGetter(ROOT_SQL_NODE)) {
         //修改参数值
         SqlNode sqlNode = (SqlNode) metaMappedStatement.getValue(ROOT_SQL_NODE);
         getBoundSql(statement.getConfiguration(), boundSql.getParameterObject(), sqlNode);
     }
     return invocation.proceed();
 }

 @Override
 public Object plugin(Object target) {
     return Plugin.wrap(target, this);
 }

 @Override
 public void setProperties(Properties properties) {
 }


 public static BoundSql getBoundSql(Configuration configuration, Object parameterObject, SqlNode sqlNode) {
     DynamicContext context = new DynamicContext(configuration, parameterObject);
     sqlNode.apply(context);
     String countextSql = context.getSql();
     SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
     Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
     String sql = modifyLikeSql(countextSql, parameterObject);
     SqlSource sqlSource = sqlSourceParser.parse(sql, parameterType, context.getBindings());

     BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
     for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
         boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
     }
     return boundSql;
 }

 public static String modifyLikeSql(String sql, Object parameterObject) {
     if (parameterObject instanceof Map) {
     } else {
         return sql;
     }
     if (!sql.toLowerCase().contains("like"))
         return sql;
     String reg = "\\bILIKE\\b.*\\#\\{\\b.*\\}";
     Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
     Matcher matcher = pattern.matcher(sql);

     List<String> replaceFiled = new ArrayList<String>();

     while (matcher.find()) {
         int n = matcher.groupCount();
         for (int i = 0; i <= n; i++) {
             String output = matcher.group(i);
             if (output != null) {
                 String key = getParameterKey(output);
                 if (replaceFiled.indexOf(key) < 0) {
                     replaceFiled.add(key);
                 }
             }
         }
     }
     //修改参数
     Map<String, Object> paramMab = (Map) parameterObject;
     for (String key : replaceFiled) {
         Object val = paramMab.get(key);
         if (val != null && val instanceof String && (val.toString().contains("%") || val.toString().contains("_"))) {
             val = val.toString().replaceAll("%", "/%").replaceAll("_", "/_");
             paramMab.replace(key.toString(), val);
         }
     }
     return sql;
 }

 private static String getParameterKey(String input) {
     String key = "";
     String[] temp = input.split("#");
     if (temp.length > 1) {
         key = temp[1];
         key = key.replace("{", "").replace("}", "").split(",")[0];
     }
     return key.trim();
 }
 }
