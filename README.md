基于https://code.fanruan.com/hugh/video-demo-sso-login实现的帆软报表supOS单点登录插件

#### 适用于：

帆软 v10.0
supOS v3.5.x v4.x



#### 插件包构建：

执行命令

```shell
gradle zip 
```

执行完毕后，可以在插件源码目录/build/install目录下看到构建好的插件安装包(一个zip文件)



#### 插件包安装：

进入帆软报表后台 -- 管理系统 -- 插件管理功能下进行安装

**注意：**此插件不支持热加载，安装完成后需重启帆软服务器https://wiki.fanruan.com/display/PD/com.fr.decision.fun.GlobalRequestFilterProvider



#### 配置：

1. 在supOS中生成ak/sk
2. 进入帆软后台 -- 管理系统 -- 系统管理 -- 常规 下配置APP ID（ak）、SECRET（sk）、domain（supOS域名，最后不带/）
3. 进入帆软后台 -- 管理系统 -- 系统管理  -- 模板认证 下开启”模板认证“，如认证方式为”角色权限认证“，还需为角色分配报表权限
3. 在supOS组态期 -- 系统管理 -- 菜单配置 下 新增菜单，菜单url配置为帆软报表链接，例如http://localhost:8075/webroot/decision/view/report?viewlet=GettingStarted.cpt

4. 在supOS给用户/角色分配相应的菜单权限即可

注意：supOS、帆软报表用户名需一致，用户同步请自行解决



#### 其他说明：

   默认拦截的是报表请求入口，如拦截其他页面

   ```
   /decision/view/form/*
   /decision/view/report/*
   ```









------


我是小福，你们懂的