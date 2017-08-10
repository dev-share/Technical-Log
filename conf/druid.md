Druid与Spring集成并监控请求
1.web.xml配置
  <!-- Druid数据源过滤器 -->
  <filter>
    <filter-name>DruidFilter</filter-name>
    <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
    <async-supported>true</async-supported>
    <init-param>
      <param-name>exclusions</param-name>
      <param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value>
    </init-param>
    <init-param>
      <param-name>profileEnable</param-name>
      <param-value>true</param-value>
    </init-param>
    <init-param>
      <param-name>principalSessionName</param-name>
      <param-value>principal</param-value>
    </init-param>
    <init-param>
      <param-name>principalCookieName</param-name>
      <param-value>principal</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>DruidFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
  <!-- Druid请求分发调度器 -->
  <servlet>
    <servlet-name>DruidStatView</servlet-name>
    <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
    <init-param>
      <param-name>resetEnable</param-name>
      <param-value>false</param-value>
    </init-param>
    <async-supported>true</async-supported>
  </servlet>
  <servlet-mapping>
    <servlet-name>DruidStatView</servlet-name>
    <url-pattern>/druid/*</url-pattern>
  </servlet-mapping>
  
  2.Spring数据源配置及Druid事物
  
  <!-- 目标数据源配置 -->
    <bean id="mainDataSource" class="com.alibaba.druid.pool.DruidDataSource"
        init-method="init" destroy-method="close">
        <!-- 基本属性 url、user、password -->
        <property name="url" value="${db.url}" />
        <property name="username" value="${db.username}" />
        <property name="password" value="${db.password}" />

        <!-- 配置初始化大小、最小、最大 -->
        <property name="initialSize" value="20" />
        <property name="minIdle" value="50" />
        <property name="maxActive" value="100" />

        <!-- 配置获取连接等待超时的时间 -->
        <property name="maxWait" value="600000" />

        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="600000" />

        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="600000" />

        <property name="validationQuery" value="SELECT 'x'" />
        <property name="testWhileIdle" value="true" />
        <property name="testOnBorrow" value="false" />
        <property name="testOnReturn" value="false" />

        <!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
        <property name="poolPreparedStatements" value="false" />
        <property name="maxPoolPreparedStatementPerConnectionSize" value="20" />

        <!-- 配置监控统计拦截的filters -->
        <property name="filters" value="stat,log4j" />
        
        <!-- 打开removeAbandoned功能,如果连接超过30分钟未关闭，就会被强行回收，并且日志记录连接申请时输出错误日志-->
        <property name="removeAbandoned" value="true" /> <!-- 打开removeAbandoned功能 -->
    	<property name="removeAbandonedTimeout" value="1800" /> <!-- 1800秒，也就是30分钟 -->
    	<property name="logAbandoned" value="true" /> <!-- 关闭abanded连接时输出错误日志 -->
    	<!-- 合并多个DruidDataSource的监控数据 -->
    	<property name="useGlobalDataSourceStat" value="true" />
    </bean>


<!-- 配置Druid和Spring关联监控配置 -->
	<bean id="druid-stat-interceptor" class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor"/>
	<bean id="druid-stat-pointcut" class="org.springframework.aop.support.JdkRegexpMethodPointcut" scope="prototype">
	    <property name="pattern" value=".*service.*"/>
	</bean>
	<aop:config proxy-target-class="true">
	    <aop:advisor advice-ref="druid-stat-interceptor" pointcut-ref="druid-stat-pointcut" />
	</aop:config>
