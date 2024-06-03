# 不同 SSO Client 配置不同秘钥

在校验 ticket、单点注销等操作发起的 http 调用时，需要配置秘钥参数，像这样：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
    sign:
        # API 接口调用秘钥
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!------------- tab:properties 风格  ------------->
``` properties
# 接口调用秘钥 
sa-token.sign.secret-key=kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!---------------------------- tabs:end ---------------------------->


如果 SSO Client 端和 SSO Server 端配置的秘钥不同，则无法调通请求，显示无效签名：
``` js
{
  "code": 500,
  "msg": "无效签名：9f1b453817bfeac56d2f772a66c01eb2",
  "data": null
}
```

如果你有多个 SSO Client，你可能想让每个应用配置不同的秘钥，让它们彼此之间不能互相“冒充”，怎么做呢？

### 1、首先在 SSO Client 端，你需要配置上不同的 Client 标识参数：

例如在 client1 我们配置上：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token:
    sso-client:
        # 当前 client 标识
        client: sso-client1
        # ... 
    sign:
        # sso-client1 使用的秘钥 
        secret-key: secret-key-xxxx-1
```
<!------------- tab:properties 风格  ------------->
``` properties
# 当前 client 标识
sa-token.sso-client.client=sso-client1

# sso-client1 使用的秘钥 
sa-token.sign.secret-key=secret-key-xxxx-1
```
<!---------------------------- tabs:end ---------------------------->


在 client2 我们配置上：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token:
    sso-client:
        # 当前 client 标识
        client: sso-client2
        # ... 
    sign:
        # sso-client2 使用的秘钥 
        secret-key: secret-key-xxxx-2
```
<!------------- tab:properties 风格  ------------->
``` properties
# 当前 client 标识
sa-token.sso-client.client=sso-client2

# sso-client2 使用的秘钥 
sa-token.sign.secret-key=secret-key-xxxx-2
```
<!---------------------------- tabs:end ---------------------------->


### 2、然后在 SSO Server 端，重写获取秘钥的函数

在 SSO Server 端新建 `CustomSaSsoServerTemplate.java`，继承 `SaSsoServerTemplate`，重写其 `getSignTemplate` 函数：

``` java
/**
 * 自定义 SaSsoServerTemplate 子类 
 */
@Component
public class CustomSaSsoServerTemplate extends SaSsoServerTemplate {

	// 存储所有 client 的秘钥 
    static Map<String, SaSignTemplate> signMap = new HashMap<>();
    static {
        signMap.put("sso-client1", new SaSignTemplate(new SaSignConfig("secret-key-xxxx-1")));
        signMap.put("sso-client2", new SaSignTemplate(new SaSignConfig("secret-key-xxxx-2")));
        signMap.put("sso-client3", new SaSignTemplate(new SaSignConfig("secret-key-xxxx-3")));
		// ... 
    }

    @Override
    public SaSignTemplate getSignTemplate(String client) {
        // 先从自定义的 signMap 中获取
        SaSignTemplate saSignTemplate = signMap.get(client);
        if (saSignTemplate != null) {
            return saSignTemplate;
        }
        // 找不到就返回全局默认的 SaSignTemplate
        return SaManager.getSaSignTemplate();
    }
}
```

至此完成，其它代码一切照旧。


