package io.github.notsyncing.cowherd.service;

import io.github.notsyncing.cowherd.annotations.ContentType;
import io.github.notsyncing.cowherd.annotations.Exported;
import io.github.notsyncing.cowherd.annotations.httpmethods.HttpAnyMethod;
import io.github.notsyncing.cowherd.annotations.httpmethods.HttpGet;
import io.github.notsyncing.cowherd.models.CowherdServiceInfo;
import io.github.notsyncing.cowherd.models.UploadFileInfo;
import io.github.notsyncing.cowherd.utils.FileUtils;
import io.github.notsyncing.cowherd.utils.RouteUtils;
import io.github.notsyncing.cowherd.utils.StringUtils;
import io.vertx.core.http.HttpServerRequest;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CowherdAPIService extends CowherdService
{
    private static Date injectServiceScriptGenerationTime = null;

    @HttpAnyMethod
    @Exported
    public CompletableFuture gateway(String __service__, String __action__, HttpServerRequest request,
                                     Map<String, List<String>> __parameters__, List<UploadFileInfo> __uploads__)
    {
        return delegateTo(__service__, __action__, request, __parameters__, __uploads__);
    }

    @HttpGet
    @Exported
    @ContentType("text/javascript")
    public String injectServices(String base, String service, HttpServerRequest request) throws IOException
    {
        if ((!StringUtils.isEmpty(request.getHeader("If-Modified-Since"))) && (injectServiceScriptGenerationTime != null)) {
            request.response().putHeader("Last-Modified",
                    StringUtils.dateToHttpDateString(injectServiceScriptGenerationTime));
            request.response().setStatusCode(304).end();
            return null;
        }

        injectServiceScriptGenerationTime = new Date();
        request.response().putHeader("Last-Modified", StringUtils.dateToHttpDateString(injectServiceScriptGenerationTime));

        if (StringUtils.isEmpty(base)) {
            base = "~/";
        }

        String js = "(function () {\n";

        js += FileUtils.getInternalResourceAsString("/META-INF/resources/webjars/es6-promise/3.0.2/dist/es6-promise.min.js") + "\n\n";
        js += FileUtils.getInternalResourceAsString("/META-INF/resources/webjars/reqwest/2.0.5/reqwest.min.js") + "\n\n";

        for (CowherdServiceInfo info : ServiceManager.getServices()) {
            if ((!StringUtils.isEmpty(service)) && (!info.getFullName().equals(service))) {
                continue;
            }

            if (!StringUtils.isEmpty(info.getNamespace())) {
                js += "if (!window." + info.getNamespace() + ") {\n";
                js += "window." + info.getNamespace() + " = {};\n";
                js += "}\n";
                js += "window." + info.getNamespace() + "." + info.getName() + " = {};\n";
            } else {
                js += "window." + info.getName() + " = {};\n";
            }

            for (Method m : info.getServiceClass().getMethods()) {
                if (!m.isAnnotationPresent(Exported.class)) {
                    continue;
                }

                if (!StringUtils.isEmpty(info.getNamespace())) {
                    js += "window." + info.getNamespace() + "." + info.getName();
                } else {
                    js += "window." + info.getName();
                }

                js += "." + m.getName() + " = function (";

                if (m.getParameterCount() > 0) {
                    for (Parameter p : m.getParameters()) {
                        js += p.getName() + ", ";
                    }

                    js = js.substring(0, js.length() - 2);
                }

                js += ") {\n";
                js += "return new Promise(function (resolve, reject) {\n";
                js += "reqwest({\n";
                js += "url: '" + base + "api/gateway',\n";
                js += "method: '" + RouteUtils.getActionHttpMethodString(m) + "',\n";
                js += "data: {\n";
                js += "__service__: '" + info.getFullName() + "',\n";
                js += "__action__: '" + m.getName() + "'";

                if (m.getParameterCount() > 0) {
                    js += ",\n";

                    for (Parameter p : m.getParameters()) {
                        js += p.getName() + ": " + p.getName() + ",\n";
                    }

                    js = js.substring(0, js.length() - 2);
                }

                js += "\n";
                js += "},\n";
                js += "error: function (err) {\nreject(err);\n},\n";
                js += "success: function (resp) {\nresolve(resp.responseText ? resp.responseText : resp);\n}\n";
                js += "});\n";
                js += "});\n";
                js += "};\n";
            }
        }

        js += "})();\n";
        return js;
    }
}
