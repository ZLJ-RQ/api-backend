package com.api.apigateway.filter;

import com.api.apiclientsdk.utils.SignUtil;
import com.api.common.model.entity.InterfaceInfo;
import com.api.common.model.entity.User;
import com.api.common.service.InnerInterfaceInfoService;
import com.api.common.service.InnerUserInterfaceInfoService;
import com.api.common.service.InnerUserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_BLACK_LIST= Arrays.asList("");

    private static final String HOST_NAME="localhost:8123";

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.用户发送请求到API网关
        //2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = HOST_NAME+request.getPath().value();
        String method = request.getMethodValue();
        log.info("请求唯一标识:"+request.getId());
        log.info("请求路径:"+path);
        log.info("请求方法:"+method);
        log.info("请求参数:"+request.getQueryParams());
        String requestAddress = request.getRemoteAddress().getHostString();
        log.info("请求地址:"+requestAddress);
        //3.黑白名单
        ServerHttpResponse response = exchange.getResponse();
        if (IP_BLACK_LIST.contains(requestAddress)){
            //设置状态码来返回相应
            return handleNoAuth(response);
        }
        //4.用户鉴权(判断ak,sk是否合法)
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String  nonce= headers.getFirst("nonce");
        String body = URLDecoder.decode(headers.getFirst("body"), "utf-8");
        String timestamp = headers.getFirst("timestamp");
        String sign =headers.getFirst("sign");
        // 从数据库中查是否已分配给用户accessKey
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        if (accessKey!=null&&!accessKey.equals(invokeUser.getAccessKey())){
            return handleNoAuth(response);
        }
        if (Long.parseLong(nonce)>10000){
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过五分钟
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES=60*5L;
        if (currentTime-Long.parseLong(timestamp)>FIVE_MINUTES){
            return handleNoAuth(response);
        }
        //从数据库中查secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtil.genSign(body, secretKey);
        if (!sign.equals(serverSign)){
            return handleNoAuth(response);
        }
        //5.请求的模拟接口是否可用 远程调用接口可以考虑http请求(httpclient,feign restTemplate)或者RPC(Dubbo)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = invokeUser.getId();
        //是否还有调用次数
        Integer leftNum = innerUserInterfaceInfoService.queryLeftNum(interfaceInfoId, userId);
        if (leftNum==null||leftNum<=0){
            return handleNoAuth(response);
        }
        //6.请求转发,放行就可
        //7.响应日志 由于chain.filter是一个异步操作,所以先会执行接下来的语句,再模拟接口,这样不知道成功与否
        return handleResponse(exchange,chain,interfaceInfoId,userId);
    }


    private Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
    /**
     * @description 处理响应
     * @param exchange
     * @param chain
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @author 若倾
     * @date 2023/3/16 23:25
    */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,Long interfaceInfoId,Long userId) {
        ServerHttpResponse response = exchange.getResponse();
        try {
            //从交换机拿响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓冲区工厂,拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if(statusCode == HttpStatus.OK){
                //装饰,增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                //等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        //对象是响应式的
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //往返回值里面写数据
                            //拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                //8.调用成功, todo 接口调用次数+1 invokeCount
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                //打印日志
                                log.info("响应结果"+data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            //9.调用失败,返回规范错误码
                            handleInvokeError(response);
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                //设置response对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    //过滤器的优先级
    @Override
    public int getOrder() {
        return -1;
    }
}