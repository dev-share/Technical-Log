 package com.hollysys.smartfactory.equipmentdiagnosis.config;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
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
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;
@Component
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class}),
	@Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class,ResultHandler.class})
})
public class MyBatisInterceptor implements Interceptor {
// @Override
// public Object intercept(Invocation invocation) throws Throwable {
//	 RoutingStatementHandler handler = (RoutingStatementHandler)invocation.getTarget();
//     MetaObject mappedStatement = MetaObject.forObject(handler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_OBJECT_REFLECTOR_FACTORY);
//     MappedStatement statement = (MappedStatement) mappedStatement.getValue("delegate.mappedStatement");
//     BoundSql boundSql = (BoundSql) mappedStatement.getValue("delegate.boundSql");
//     MetaObject metaMappedStatement = MetaObject.forObject(mappedStatement, DEFAULT_OBJECT_FACTORY,DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_OBJECT_REFLECTOR_FACTORY);
////     BoundSql boundSql = statement.getBoundSql(parameter);
//     if (metaMappedStatement.hasGetter(ROOT_SQL_NODE)) {
//         //修改参数值
//         SqlNode sqlNode = (SqlNode) metaMappedStatement.getValue(ROOT_SQL_NODE);
//         getBoundSql(statement.getConfiguration(), boundSql.getParameterObject(), sqlNode);
//     }
//     return invocation.proceed();
// }
 @Override
 public Object intercept(Invocation invocation) throws Throwable {
     if (invocation.getTarget() instanceof RoutingStatementHandler) {
         RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
         MetaObject meta = MetaObject.forObject(handler, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
         Object parameterObject = handler.getParameterHandler()!=null?handler.getParameterHandler().getParameterObject():null;
         if(parameterObject!=null) {
        	 MappedStatement mappedStatement = (MappedStatement) meta.getValue("delegate.mappedStatement");
        	 BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        	 String sql = boundSql.getSql();
        	 if(sql!=null&&sql.toLowerCase().contains("like")) {
        		 MetaObject metaMapped = MetaObject.forObject(mappedStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
                 SqlNode sqlNode = (SqlNode) metaMapped.getValue("sqlSource.rootSqlNode");
        		 handle(mappedStatement.getConfiguration(), (Map<String, Object>)boundSql.getParameterObject(), sqlNode);
        	 }
         }
     }
     return invocation.proceed();
 }
 @Override
 public Object plugin(Object target) {
     return Plugin.wrap(target, this);
 }

 @Override
 public void setProperties(Properties properties) {
	 properties.setProperty("supportMethodsArguments", "true");
	 properties.setProperty("rowBoundsWithCount", "true");
	 properties.setProperty("reasonable", "true");
 }


 public static BoundSql handle(Configuration configuration, Map<String, Object> parameterObject, SqlNode sqlNode) {
     DynamicContext context = new DynamicContext(configuration, parameterObject);
     sqlNode.apply(context);
     String countextSql = context.getSql();
     SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
     Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
//     String msql = modifyLikeSql(countextSql, parameterObject);
     SqlSource sqlSource = sqlSourceParser.parse(countextSql, parameterType, context.getBindings());
     context.appendSql(" and 1=1");
     BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
     String key = "keyword";
     for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
    	 Object value = entry.getValue();
    	 if(entry.getKey()!=null&&entry.getValue()!=null) {
    		 if(key.equals(entry.getKey())) {
    			 value = value.toString().replaceAll("_", "/_");
    		 }
    	 }
    	 context.bind(entry.getKey(), value);
         boundSql.setAdditionalParameter(entry.getKey(), value);
     }
     for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
    	 System.out.println("key:"+entry.getKey()+",value:"+entry.getValue()+",cvalue:"+parameterObject.get(entry.getKey())); 
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
//     Matcher matcher = Pattern.compile("\\bLIKE\\b.*\\#\\{\\b.*\\}", Pattern.CASE_INSENSITIVE).matcher(sql);
//     matcher = matcher.matches()?matcher:Pattern.compile("* LIKE ?", Pattern.CASE_INSENSITIVE).matcher(sql);
//
//     List<String> replaceFiled = new ArrayList<String>();
//
//     while (matcher.find()) {
//         int n = matcher.groupCount();
//         for (int i = 0; i <= n; i++) {
//             String output = matcher.group(i);
//             if (output != null) {
//                 String key = getParameterKey(output);
//                 if (replaceFiled.indexOf(key) < 0) {
//                     replaceFiled.add(key);
//                 }
//             }
//         }
//     }
     //修改参数
     Map<String, Object> paramMab = (Map) parameterObject;
     String keyv = "keyword";
     if(paramMab!=null&&paramMab.containsKey(keyv)) {
    	 Object val = paramMab.get(keyv);
    	 if (val != null && val instanceof String && (val.toString().contains("%") || val.toString().contains("_"))) {
             val = val.toString().replaceAll("%", "/%").replaceAll("_", "/_");
             paramMab.replace(keyv.toString(), val);
         }
     }
//     for (String key : replaceFiled) {
//         Object val = paramMab.get(key);
//         if (val != null && val instanceof String && (val.toString().contains("%") || val.toString().contains("_"))) {
//             val = val.toString().replaceAll("%", "/%").replaceAll("_", "/_");
//             paramMab.replace(key.toString(), val);
//         }
//     }
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
