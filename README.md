 dorado 接口安全扩展

dorado 封装了Ajax请求，导致在权限处理上与传统Web项目还是有很大的区别。dorado下，暴露一个服务可以通过`@Expose`、`@DataProvider`、`@DataResolver`等注解。

比如，我想提供一个接口，参数为`name`, 返回值为`"Hello " + name`, 我只需要写一个Java方法，同时把类注册到Spring上下文，并在方法上标注`@Expose`。
```Java
@Service
public class DemoController {
  @Expose
  public String hello(String name) {
    return "Hello " + name;
  }
}
```
像这样的一个接口，在任何一个dorado页面都可以通过构建一个AjaxAction，并配置service为beanId#方法名的形式 `demoController#hello`， 就可以调用这个方法。

```JavaScript
new dorado.widget.AjaxAction({
    service: "demoController#hello",
    parameter: "xobo"
}).execute(function(result) {
    console.log(result)
})
```

dorado封装了HTTP请求，所有的请求统一发给URL `/dorado/view-service`，然后由报文内容决定调用哪一个接口。

dorado 的这一特性导致，无法通过拦截URL的方式管理接口的权限，只能通过AOP的方式去拦截方法从而实现接口的管理。每个页面都会提供大量的接口，不同的页面可能会使用相同的接口。会导致接口权限的配置变得特别复杂。

从简化配置的角度，可以通过解析dorado view建立dorado接口与页面的映射，通过判断是否有权限访问接口所在的页面来判断能否访问接口。然后对需要更细粒度管理的接口做一个额外的配置。


## 添加依赖
在pom.xml 里添加 repository
```XML
<repositories>
	<repository>
		<id>xobo-repo</id>
		<url>https://nexus.xobo.org/content/groups/public/</url>
	</repository>
</repositories>
```
同时添加依赖
```XML
<dependency>
    <groupId>org.xobo.dorado</groupId>
    <artifactId>bdf2-dorado-exposedservice-security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
## 属性设置
默认设置已经满足日常使用的需求，如果需要额外定制参考:

| 属性 | 默认值 | 描述 |
|---|---|---|
| dorado.exposedservice.mapping.enablecache |true  |  true, 启用Spring Cache; false 不使用缓存，每次重新加载(很慢)。|
| dorado.exposedservice.errorhandle |0  | 如果没有权限时，对应的行为：0 只记录日志; 1 跳过方法执行; 2 抛出 AccessDeniedException 异常。 |
| dorado.exposedservice.whitelist |  (空)|  白名单, 忽略权限拦截的service。多个service用`,`分隔。|

