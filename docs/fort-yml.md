## fort.yml

fort sdk 的配置文件，以maven项目为例，将fort.yml放置在`src/main/resource/`目录内

fort.yml sample:

```
# ===================================================================
# Fort configuration
# ===================================================================

# ===================================================================
# Standard Fort properties.
# Full reference is available at:
# http://180.167.77.58:30000/fort/fort-sdk/blob/master/docs/fort-yml.md
# ===================================================================

fort:
    # 应用模块
    app:
        # fort server 的基础url
        server-base: http://localhost:8080
        # 应用 key
        app-key: soqcnpjhxqku
        # 应用 secret
        app-secret: 6ddeh9nrgtjd
    # 身份验证模块
    authentication:
        # 用户没有权限访问时，重定向的地址
        unauthorized-return: /unauthorized.html
        # 登录
        login:
            # 登录URL
            url: /fort/login
            # 登录成功时，重定向的地址
            success-return: /index.html
            # 登录失败时，重定向的地址
            error-return: /login.html?error=1
            # 登录页面地址
            login-view: /login.html
        # 登出
        logout:
            # 登出URL
            url: /fort/logout
            # 登出成功时，重定向的地址
            success-return: /index.html
    # 用户模块
    user:
        # 创建新用户时，用户拥有的角色名，多个使用,隔开
        default-roles: user
        # 创建新用户时，用户所属组名，多个使用,隔开
        default-groups: dev
```